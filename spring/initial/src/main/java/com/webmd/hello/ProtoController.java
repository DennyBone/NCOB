package com.webmd.hello;

import com.webmd.common.mq.MqComponent;
import com.webmd.proto.MotionCommandProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import proto.ProtoMessage;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.concurrent.ExecutorService;

@Controller
@Component
@Slf4j
public class ProtoController extends MqComponent
{
    private ZMQ.Socket dealerSocket;

    @Autowired
    public ProtoController(@Value("${mq.router.host}") String host, @Value("${mq.router.port}") String port, ExecutorService executor, ZContext context)
    {
        super(host, port, executor, context);
    }

    @Override
    public void run()
    {
        //final String backendAddress = "tcp://*:61616";
        final String routerAddress = "tcp://localhost:61615";
        log.info("Hello from ProtoController");

        //  Prepare our context and sockets
        ZMQ.Context selfContext = ZMQ.context(1);

        // open dealer socket and connect to router
        dealerSocket = selfContext.socket(ZMQ.DEALER);
        dealerSocket.connect(routerAddress);
        log.info("Dealer Client connected to : " + routerAddress);

        // Send initialization message
        ZMsg msg = new ZMsg();
        msg.add("INIT");
        msg.add("ProtoControllerClient");
        log.info("Sending Msg " + msg);
        msg.send(dealerSocket);

        // Check for successful registration message
        ZMsg response = ZMsg.recvMsg(dealerSocket);
        log.info("Received Response Msg {}", response);
        //response.popString(); // connection ID
        if(!response.popString().equalsIgnoreCase("Registered"))
        {
            // try again - did not register successfully
            log.info("try again");
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        msg.add("ProtoControllerClient");
        msg.add("ProtoControllerMsg");
        log.info("Sending Msg " + msg);
        msg.send(dealerSocket);

        while (true)
        {
            response = ZMsg.recvMsg(dealerSocket);
            log.info("Received Response Msg {}", response);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            msg.add("MqDealerWorkerClient");
            msg.add("ProtoControllerMsg");
            log.info("Sending Msg " + msg);
            msg.send(dealerSocket);
        }

        // Create 0mq sockets for the controller to use

        // dealer to dealer can be used if we're only talking to one component(the broker)

        // open dealer socket and connect to broker backend(dealer)
        //dealerSocket = context.createSocket(ZMQ.DEALER);
        //dealerSocket.connect(backendAddress);
    }

    @GetMapping("/proto")
    public String greetingForm(Model model) {
        System.out.println("GET");
        model.addAttribute("protoMessage", new ProtoMessage());
        return "proto";
    }

    @PostMapping("/proto")
    public String greetingSubmit(@ModelAttribute(name = "protoMessage") ProtoMessage protoMessage) {
        // This method receives the form data that was submitted to /proto.html
        // Using the given data, use protobuf to write to the addresses.dat file
        // After that, consider writing this data to a DB instead

        log.info("Throttle {}", protoMessage.getThrottle());
        log.info("Servo {}", protoMessage.getServo());

        // construct motioncommandproto builder
        MotionCommandProto.Command.Builder motionCmdBuilder = MotionCommandProto.Command.newBuilder();
        motionCmdBuilder.setThrottle(protoMessage.getThrottle());
        motionCmdBuilder.setServo(protoMessage.getServo());

        // protobuf messages are immutable; builders must be used to set the message fields
        // building the builder object returns the proto msg with the set fields
        MotionCommandProto.Command motionCmd = motionCmdBuilder.build();

        System.out.println("motionCmd.getThrottle: " + motionCmd.getThrottle());
        System.out.println("motionCmd.getServo: " + motionCmd.getServo());

        // now send the proto message over a zmq socket
        // create a 'worker' socket that connects to the backend/dealer

        return "proto";
    }

}
