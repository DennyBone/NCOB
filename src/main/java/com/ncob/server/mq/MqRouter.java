//package com.webmd.server.mq;
//
//import com.ncob.common.mq.MqComponent;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.jmx.export.annotation.ManagedOperation;
//import org.springframework.jmx.export.annotation.ManagedResource;
//import org.springframework.stereotype.Component;
//import org.zeromq.ZContext;
//import org.zeromq.ZMQ;
//import org.zeromq.ZMsg;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//
//@ManagedResource
//@Component
//@Slf4j
//public class MqRouter extends MqComponent {
//    private final Map<String, byte[]> connectionIdToClientId = new HashMap<>();
//    private ZMQ.Socket routerSocket;
//
//    @Autowired
//    public MqRouter(@Value("${mq.router.host}") String host, @Value("${mq.router.port}") String port, ExecutorService executor, ZContext context) {
//        super(host, port, executor, context);
//    }
//
//    @Override
//    public void run()
//    {
//        log.info("Hello from MqRouter");
//        final String address = getAddress(); // "tcp://*:61615"
//
//        try
//        {
//            routerSocket = context.createSocket(ZMQ.ROUTER);
//            routerSocket.bind(address);
//            routerSocket.setRouterMandatory(true);//let us know if connection id does not exist
//            log.info("Mq router port bound to {}", address);
//
//            while (true)
//            {
//                ZMsg request = ZMsg.recvMsg(routerSocket); // blocks
//                if (request == null)
//                {
//                    log.error("interrupted");
//                    break;
//                }
//                log.debug("Received Msg {}", request);
//
//                //router pre-pends unique id in form of 5 bytes for client connections
//                // Why is request.poll being used here?
//                byte[] connectionId = request.poll().getData();//IMPORTANT router expects key in 5 bytes when sending, do NOT read key as string
//                String nullFrame = request.poll().toString();//Annoyingly REQ socket will place null frame before message, DEALER will not
//                String firstFrame;
//                String msg;
//                if(nullFrame.equals(""))
//                {
//                    firstFrame = request.poll().toString();
//                }
//                else
//                {
//                    firstFrame = nullFrame;
//                }
//
//                msg = request.poll().toString();
//
//                // Initialization message - add connection and client ids to map
//                if(firstFrame.equalsIgnoreCase("INIT"))
//                {
//                    connectionIdToClientId.put(msg, connectionId);
//                    sendMessage("Registered", msg); // send success msg back to client
//                }
//
//                // route message to correct client/service
//                else
//                {
//                    sendMessage(msg, firstFrame);
//                }
//            }
//        } catch (Exception e) {
//            log.error("mq component failed", e);
//        } finally {
//            context.close();
//        }
//    }
//
//    @ManagedOperation
//    public HashSet<String> getConnectedClientIds() {
//        return new HashSet<>(connectionIdToClientId.keySet());
//    }
//
//    @ManagedOperation
//    public void sendMessage(String message, String clientId) {
//        byte[] connectionId = connectionIdToClientId.get(clientId);
//        if (connectionId == null) {
//            throw new IllegalArgumentException("No client connection for client " + clientId);
//        }
//        log.info("Sending message: {}", message);
//        ZMsg zMsg = new ZMsg();
//        zMsg.add(connectionId);
//        //zMsg.add("");//add null frame for REQ socket as required, assume implementation of a DEALER socket is smart enough to check for it.
//        zMsg.add(message);
//        boolean success = zMsg.send(routerSocket);//strips connection id from first frame and routes message accordingly
//        if (!success) {
//            log.error("Send to client {} unsuccessful", connectionId);
//        }
//    }
//
//}
