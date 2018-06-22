//package com.ncob.common.mq;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.zeromq.ZContext;
//import org.zeromq.ZFrame;
//import org.zeromq.ZMQ;
//import org.zeromq.ZMsg;
//
///*
//This class provide methods to connect and talk to the broker
// */
//public class MqWorker
//{
//    @Value("${mq.broker.backend.address}")
//    private String address;
//
//    private ZMQ.Socket dealerSocket;
//    private ZContext context;
//
//    @Autowired
//    public MqWorker(ZContext context)
//    {
//        this.context = context;
//        openSocket();
//    }
//
//    private void openSocket()
//    {
//        // open dealer socket and connect to router
//        // inproc sockets need to use the same context
//        dealerSocket = context.createSocket(ZMQ.DEALER);
//        dealerSocket.connect(address);
//    }
//
//    public void sendString()
//    {
//        //  The DEALER socket gives us the address envelope and message
//        //ZMsg msg = ZMsg.recvMsg(dealerSocket);
//        ZMsg msg = ZMsg.recvMsg(dealerSocket, ZMQ.DONTWAIT);
//        ZFrame address = msg.pop();
//        ZFrame content = msg.pop();
//        assert (content != null);
//        msg.destroy();
//
//        //  Send 0..4 replies back
//        int replies = rand.nextInt(5);
//        for (int reply = 0; reply < replies; reply++) {
//            //  Sleep for some fraction of a second
//            try {
//                Thread.sleep(rand.nextInt(1000) + 1);
//            } catch (InterruptedException e) {
//            }
//            address.send(dealerSocket, ZFrame.REUSE + ZFrame.MORE);
//            content.send(dealerSocket, ZFrame.REUSE);
//        }
//        address.destroy();
//        content.destroy();
//    }
//
//    public void cleanup()
//    {
//        dealerSocket.close();
//        context.destroy();
//    }
//}
