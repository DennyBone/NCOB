package com.ncob.server.mq;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.ncob.common.mq.MqComponent;
import com.ncob.mongo.robots.Robot;
import com.ncob.mongo.robots.RobotRepository;
import com.ncob.mongo.robots.RobotRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZMQ.Poller;

import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * This class will start the broker when the application starts. The broker will handle registering new connections
 * and forwarding messages from frontend(clients) to backend(controllers)
 */
@Component
@Slf4j
public class MqBroker extends MqComponent
{
    // Maps to relate client/controller names to their socket IDs(invented by router sockets)
    private static final BiMap<String, byte[]> frontendMap = HashBiMap.create();
    private static final BiMap<String, byte[]> backendMap = HashBiMap.create();

    // Map used to retrieve socket router IDs for a given robot and socket
    private static final HashMap<String, HashMap<String, byte[]>> robotNameToSocketId = new HashMap<>();

    private static final String SUCCESS = "0";
    private static final String FAILURE = "-1";

    private static Socket frontend;
    private static Socket backend;

    @Autowired
    private RobotRepository robotRepository; // used in registration process

    @Autowired
    public MqBroker(@Value("${mq.broker.frontend.host}") String host, @Value("${mq.broker.frontend.port}") String port, ExecutorService executor, ZContext context)
    {
        super(host, port, executor, context);
    }

    @Override
    public void run()
    {
        // Retrieve client and controller facing endpoints
        final String frontendAddress = getAddress(); // "tcp://*:61615"
        final String backendAddress = getInprocAddress(); //"inproc://backend"

        //  Socket facing clients
        frontend = context.createSocket(ZMQ.ROUTER);
        frontend.setRouterMandatory(true); //let us know if connection id does not exist
        frontend.bind(frontendAddress);

        //  Socket facing services, connected with inproc socket
        backend = context.createSocket(ZMQ.ROUTER);
        backend.setRouterMandatory(true); //"when you provide an unroutable identity on a send call, the socket will signal an EHOSTUNREACH error"
        backend.bind(backendAddress);

        while (!Thread.currentThread().isInterrupted())
        {
            //  Initialize poll set
            Poller items = context.createPoller(2);
            //  Always poll for worker activity on backend
            items.register(backend, Poller.POLLIN);
            // Always poll for client activity on frontend
            items.register(frontend, Poller.POLLIN);
            log.info("Broker has started");

            if (items.poll() < 0)
                break;      //  Interrupted

            //  poll backend(controllers) router socket
            if (items.pollin(0))
            {
                ZMsg msg = ZMsg.recvMsg(backend);
                if (msg == null)
                    break;  //  Interrupted

                log.info("Backend msg: {}",  msg);
                ZFrame identity = msg.unwrap(); // retrieves and removes the ID and null frame (if exists) from the msg
                ZFrame frame = msg.getFirst();

                // check if it's a registration message, otherwise forward to correct client
                if(frame.toString().equals("Register"))
                {
                    // place new robot in map and add to DB
                    // might need a way to persist the bimaps in case the server fails and the client stays up; or we could just have clients re-register
                    registerRobot(identity, msg , backendMap, backend);

                    frame.destroy();
                }
                else
                {
                    lookupAndForwardMsg(backendMap, frontendMap, msg, identity, frontend);

                    // BROKER WILL SHUTTLE REPLIES W/O USING A LOOKUP TABLE. THIS MEANS WHEN A REQ IS RECEIVED, THE ADDRESS OF THE SENDER NEEDS TO BE SAVED AND ADDED TO ALL SUBSEQUENT REPLIES
                    // SO THE ROUTER SOCKETS CAN DIRECT IT TO THE PROPER PLACE. ROBOT REQUESTS TBD - HOW WILL A ROBOT(OR THE BROKER) KNOW WHERE THE ROBOT'S REQ NEEDS TO GO? CONTROLLERS(BROWSER)
                    // CAN SELECT FROM A LIST OF REGISTERED ROBOTS/SOCKETS TO DETERMINE WHERE TO SEND THE REQ.


                }
            }

            // poll Frontend router socket
            if (items.pollin(1))
            {
                ZMsg msg = ZMsg.recvMsg(frontend);
                if (msg == null)
                    break;  //  Interrupted

                log.info("Frontend msg: {}", msg);
                ZFrame identity = msg.unwrap(); // retrieves and removes the ID and null frame (if exists) from the msg
                ZFrame frame = msg.getFirst();

                // check if it's a registration message, otherwise forward to correct controller
                if(frame.toString().equals("Register"))
                {
                    // place new robot in map
                    // might need a way to persist the bimaps in case the server fails and the client stays up; or we could just have clients re-register
                    registerRobot(identity, msg , frontendMap, frontend);
                    frame.destroy();
                }
                else
                {
                    lookupAndForwardMsg(frontendMap, backendMap, msg, identity, backend);
                }
            }
        }

        //  Clean up
        frontend.close();
        backend.close();
        context.destroy();
    }

    /**
     * This method handles registration messages. The ID of the sender and the given robot name
     * are added to a bimap for later bidirectional retrieval. Also, the robot and its socket ID(s)
     * are persisted in the mongo DB
     *
     * @param id ID of sender
     * @param msg Unwrapped message from sender
     * @param biMap Frontend or backend depending on where the message came from
     * @param socket Socket to send SUCCESS message on
     */
    private void registerRobot(ZFrame id, ZMsg msg, BiMap<String, byte[]> biMap, Socket socket)
    {
        // reg msg -> [socket ID(added by router), 'Register', robotName, socketName]

        // remove 'register' frame
        msg.pop();

        // retrieve robot name and socket name
        String robotName = msg.popString();
        String socketName = msg.popString();
        log.info("connection ID: {}", id.getData());
        log.info("robot name: {}", robotName);
        log.info("socket name: {}", socketName);

        // add new connection to frontend/backend map
        // add try/catch to catch duplicate key/value or other errors - return FAILURE
        biMap.put(robotName, id.getData());

        // check if robot already exists
        Robot robot = robotRepository.findByRobotName(robotName);
        if(robot != null)
        {
            // if so, just add the new socket
            robot.addSocket(socketName);
            robotRepository.save(robot);
        }
        else
        {
            // if not, add a new robot to the DB
            robot = new Robot(robotName);
            robot.addSocket(socketName);
            robotRepository.persistRobot(robot);
        }

        // Check if the robot is already in the in-memory map(Being in the DB doesn't mean it's in the in-memory map)
        if(robotNameToSocketId.containsKey(robotName))
        {
            // add new socket to in-memory map
            robotNameToSocketId.get(robotName).put(socketName, id.getData());
        }
        else
        {
            // add robot/socket to in-memory map for later use
            HashMap<String, byte[]> tempMap = new HashMap<>();
            tempMap.put(socketName, id.getData());
            robotNameToSocketId.put(robotName, tempMap);
            tempMap = null;
        }

        // reply that the registration was successful
        ZMsg reply = new ZMsg();
        reply.add(id);
        reply.add(SUCCESS);
        reply.send(socket);

        reply.destroy();
        id.destroy();
        msg.destroy();
    }

    /**
     * This method forwards messages from clients to controllers(and vice versa) by
     * using the sender's ID to lookup its corresponding robot name. The robot name
     * is then used to lookup the receiver's ID. Both sender and receiver need to be
     * registered with the broker beforehand.
     *
     * @param fromMap ID map for side(frontend/backend) that the message is coming from
     * @param toMap ID map for side(frontend/backend) that the message is going to
     * @param msg Message to forward
     * @param id ID of sender
     * @param socket socket(frontend/backend) to send the message out on
     */
    private static void lookupAndForwardMsg(BiMap<String, byte[]> fromMap, BiMap<String, byte[]> toMap, ZMsg msg, ZFrame id, Socket socket)
    {
        // Add error handling code for either lookup failing - client or controller not in map, etc
        // lookup robot name using the sender's connection ID
        String botName = fromMap.inverse().get(id.getData());

        // lookup receiver ID using robot name
        byte[] clientId = toMap.get(botName);

        // pass message to correct receiver
        ZFrame idFrame = new ZFrame(clientId);
        //msg.wrap(idFrame); // adds id and a null frame to front of msg
        msg.addFirst(idFrame); // this will just add the id, not a null frame like wrap will
        msg.send(socket);
    }

}

/*
If we never need to pass the message along to a REP socket, we can simply drop the empty delimiter frame at both sides, which makes things simpler.
This is usually the design I use for pure DEALER to ROUTER protocols.
 */

//0mq can set the ID of the socket, rather than the router inventing an ID for it
