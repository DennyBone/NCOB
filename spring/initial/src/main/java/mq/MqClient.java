package mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class MqClient {

    private static final Logger logger = LoggerFactory.getLogger(MqClient.class);


    public static void main(String[] args) {
        String port = "61616";
        String host = "localhost";
        String address = "tcp://" + host + ":" + port;

        logger.info("Connecting client to {}", address);
        ZContext context = new ZContext(1);
        try {
            org.zeromq.ZMQ.Socket clientSocket = context.createSocket(ZMQ.REQ);

            clientSocket.connect(address);
            logger.info("Client started");

            for (int i = 0; i < 10; ++i) {
                Thread.sleep(1000);
                String testMessage = "Hello World";
                ZMsg msg = new ZMsg();
                msg.add(testMessage);
                msg.send(clientSocket);
                logger.info("Sent Msg");
                ZMsg response = ZMsg.recvMsg(clientSocket);
                logger.info("Received Response Msg ");
                for (ZFrame frame : response) {
                    logger.info("Frame: {}", frame.toString());
                }

            }
        } catch (Exception e) {
            logger.error("mq component failed", e);
        } finally {
            context.close();
        }

    }
}
