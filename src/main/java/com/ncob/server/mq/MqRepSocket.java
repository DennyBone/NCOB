//package com.webmd.server.mq;
//
//import com.ncob.common.mq.MqComponent;
//import lombok.extern.slf4j.Slf4j;
//import org.zeromq.ZContext;
//import org.zeromq.ZFrame;
//import org.zeromq.ZMQ;
//import org.zeromq.ZMsg;
//
//import java.util.concurrent.ExecutorService;
//
//@Slf4j
//public class MqRepSocket extends MqComponent {
//
//    protected MqRepSocket(String host, String port, ExecutorService executor, ZContext context) {
//        super(host, port, executor, context);
//    }
//
//    @Override
//    public void run() {
//        final String address = getAddress();
//        try {
//            ZMQ.Socket serverSocket = context.createSocket(ZMQ.REP);
//
//            serverSocket.bind(address);
//            log.info("Mq server port bound to {}", address);
//
//            while (true) {
//                ZMsg request = ZMsg.recvMsg(serverSocket);
//                if (request == null) {
//                    log.error("interrupted");
//                    break;
//                }
//                log.info("Received Request Msg ");
//                for (ZFrame frame : request) {
//                    log.info("Frame: {}", frame.toString());
//                }
//                ZMsg response = new ZMsg();
//                response.add("Hello from the other side");
//                response.send(serverSocket);
//            }
//        } catch (Exception e) {
//            log.error("mq component failed", e);
//        } finally {
//            context.close();
//        }
//
//    }
//}
