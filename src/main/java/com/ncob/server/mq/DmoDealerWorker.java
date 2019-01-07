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

//@Component
@Slf4j
public class DmoDealerWorker extends MqComponent
{
    private ZMQ.Socket dealerSocket;

    private ZFrame clientSocketId;

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

        // allow time for client to register
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ask the broker for the robot/client socket ID; later on, the robot and socket will be selected from a dropdown on the UI
        ZMsg getIdMsg = new ZMsg();
        getIdMsg.add("GetID");
        getIdMsg.add("NCOBot");
        getIdMsg.add("socket_0");
        getIdMsg.send(dealerSocket);
        clientSocketId = ZFrame.recvFrame(dealerSocket);
        log.info("Controller received reply: " + clientSocketId);

        // Use the given ID to communicate with the desired robot/socket combo
        ZMsg testMsg = new ZMsg();
        testMsg.add(clientSocketId);
        testMsg.add("Video");
        testMsg.send(dealerSocket, true); // for example, request video data from socket_0 on NCOBot

        // continuously read replies
        while (!Thread.currentThread().isInterrupted())
        {
            ZMsg msg = ZMsg.recvMsg(dealerSocket);
            log.info("controller received msg: {}", msg);
            msg.destroy();
        }

        dealerSocket.close();
    }
}
