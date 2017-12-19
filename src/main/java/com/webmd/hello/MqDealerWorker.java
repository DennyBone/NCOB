package com.webmd.hello;

import com.webmd.common.mq.MqComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class MqDealerWorker extends MqComponent
{
    private ZMQ.Socket dealerSocket;

    @Autowired
    public MqDealerWorker(@Value("${mq.router.host}") String host, @Value("${mq.router.port}") String port, ExecutorService executor, ZContext context)
    {
        super(host, port, executor, context);
    }

    @Override
    public void run()
    {
        //final String backendAddress = "tcp://*:61616";
        final String backendAddress = "tcp://localhost:61615";
        log.info("Hello from DealerWorker");

        //  Prepare our context and sockets
        ZMQ.Context selfContext = ZMQ.context(1);

        // open dealer socket and connect to router
        dealerSocket = selfContext.socket(ZMQ.DEALER);
        dealerSocket.connect(backendAddress);
        log.info("Dealer Worker connected to : " + backendAddress);

        // Send initialization message
        ZMsg msg = new ZMsg();
        msg.add("INIT");
        msg.add("MqDealerWorkerClient");
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        msg.add("ProtoControllerClient"); // the service endpoint can be a default attribute of the protobuf msg
        msg.add("MqDealerWorkerMsg");
        log.info("Sending Msg " + msg);
        msg.send(dealerSocket);

        while (true)
        {
            response = ZMsg.recvMsg(dealerSocket);
            log.info("Received Response Msg {}", response);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            msg.add("MqDealerWorkerClient");
            msg.add("MqDealerWorkerMsg");
            log.info("Sending Msg " + msg);
            msg.send(dealerSocket);
        }


//        int count = 0;
//        while(true)
//        {
//            log.info("Waiting for message");
//            ZMsg response = ZMsg.recvMsg(dealerSocket); // blocks until a response is received
//            log.info("Received Msg {}", response);
//
//            log.info("response.size : " + response.size()); // size of message - use this to iterate
//
//            /*
//            // send msg
//            ZMsg msg = new ZMsg();
//            msg.add(response.popString()); // client ID
//            msg.add(response.popString()); // null frame
//            response.popString();
//            msg.add(response.popString()); // 1st msg part - VM Client
//            //msg.add(response.popString()); // 2nd msg part - Sup
//
//            log.info("Sending Msg 3 times : " + msg.toString());
//            msg.send(dealerSocket);
//            //msg.send(dealerSocket);
//            //msg.send(dealerSocket);
//            response.destroy();
//            msg.destroy();
//            */
//
//            /*
//            String address = response.popString();
//            String nullFrame = response.popString();
//            String id = response.popString();
//            String content = response.popString();
//            response.destroy();
//            */
//
//            ZFrame address = response.pop();
//            ZFrame nullFrame = response.pop(); // null frame
//            response.pop(); // 'VM Client'
//            ZFrame content = response.pop();
//            assert (content != null);
//            response.destroy();
//
//            log.info("address.toString : {}", address.toString());
//            log.info("content.toString : {}", content.toString());
//            address.send(dealerSocket, ZFrame.REUSE + ZFrame.MORE);
//            nullFrame.send(dealerSocket, ZFrame.REUSE + ZFrame.MORE);
//            content.send(dealerSocket, ZFrame.REUSE);
//            address.destroy();
//            nullFrame.destroy();
//            content.destroy();
//
//            /*
//            if(count == 3)
//            {
//                break;
//            }
//            count++;
//            */
//        }
//
    }
}
