package com.ncob.server.mq;

import com.ncob.common.mq.MqComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.concurrent.ExecutorService;
import java.util.Random;

@Component
@Slf4j
public class DmoDealerWorker extends MqComponent
{
    private ZMQ.Socket dealerSocket;
    private static Random rand = new Random(System.nanoTime());

    @Autowired
    public DmoDealerWorker(@Value("${mq.broker.backend.host}") String host, @Value("${mq.broker.backend.port}") String port, ExecutorService executor, ZContext context)
    {
        super(host, port, executor, context);
    }

    @Override
    public void run()
    {
        final String backendAddress = getInprocAddress(); //"inproc://backend"
        log.info("Hello from DmoDealerWorker");

        // open dealer socket and connect to router
        // inproc sockets need to use the same context
        // look into sharing contexts between threads - there's a certain/better way to do it(ZThread, shadowcontext)
        dealerSocket = context.createSocket(ZMQ.DEALER);
        dealerSocket.connect(backendAddress);
        log.info("Dealer Worker connected to : " + backendAddress);

        ZMsg registrationMsg = new ZMsg();
        registrationMsg.add("Register");
        registrationMsg.add("NCOBot");
        registrationMsg.send(dealerSocket);
        String reply = dealerSocket.recvStr();
        log.info("Controller received reply: " + reply);

        // later on, wrap registration process in a method; return 0 for success(in C client)
        if(reply.equals("0"))
        {
            // registration was successful
            registrationMsg.destroy();

            ZMsg msg = ZMsg.recvMsg(dealerSocket);
            log.info("controller received msg: {}", msg);
            msg.destroy();

            byte[] first = {1, 2, 3};
            byte[] second = {4, 5, 6};
            ZMsg testMsg = new ZMsg();
            testMsg.add("Video");
            testMsg.add(first);
            testMsg.add(second);
            testMsg.send(dealerSocket, true);

        }
        else
        {
            //reg. failed, retry
        }

        dealerSocket.close();
        //context.destroy();
    }
}
