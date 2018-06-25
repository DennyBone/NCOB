package com.ncob.client.mq;

import com.ncob.common.mq.MqComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import org.zeromq.ZPoller;

import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class DmoDealerClient extends MqComponent
{
    private ZMQ.Socket dealerSocket;

    @Autowired
    public DmoDealerClient(@Value("${mq.broker.frontend.host}") String host, @Value("${mq.broker.frontend.port}") String port, ExecutorService executor, ZContext context)
    {
        super(host, port, executor, context);
    }

    @Override
    public void run()
    {
        final String frontendAddress = getAddress(); //"tcp://localhost:61615"

        //  Prepare our context and sockets
        ZContext selfContext = new ZContext();

        // open dealer socket and connect to router
        dealerSocket = selfContext.createSocket(ZMQ.DEALER);
        // maybe eventually set a readable ID
        dealerSocket.connect(frontendAddress);
        log.info("Dealer Client connected to : " + frontendAddress);

        ZMsg registrationMsg = new ZMsg();
        registrationMsg.add("Register");
        registrationMsg.add("NCOBot");
        registrationMsg.add("socket_0");
        registrationMsg.send(dealerSocket);
        String reply = dealerSocket.recvStr();
        log.info("Client received reply: " + reply);

        // later on, wrap registration process in a method; return 0 for success(in C client)
        if(reply.equals("0"))
        {
            // registration was successful
            registrationMsg.destroy();

            // send a bunch of data
            byte[] first = {1, 2, 3};
            byte[] second = {4, 5, 6};
            ZMsg testMsg = new ZMsg();
            testMsg.add("Video");
            testMsg.add(first);
            testMsg.add(second);
            testMsg.send(dealerSocket, true);

            ZMsg msg = ZMsg.recvMsg(dealerSocket);
            log.info("client received msg: {}", msg);
            msg.destroy();

        }
        else
        {
            //reg. failed, retry
        }

        dealerSocket.close();
        selfContext.destroy();
    }
}