package mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

public class MqServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MqServer.class);

    private ExecutorService executor;

    @PostConstruct
    public void start() {
        executor.execute(this);
    }

    @Autowired
    public void setExecutor(ExecutorService executorService) {
        this.executor = executorService;
    }


    @Override
    public void run() {
        String port = "61616";
        String host = "localhost";
        String address = "tcp://" + host + ":" + port;
        ZContext context = new ZContext(1);
        try {
            org.zeromq.ZMQ.Socket serverSocket = context.createSocket(ZMQ.REP);

            serverSocket.bind(address);
            logger.info("Mq server port bound to {}", address);

            while (true) {
                ZMsg request = ZMsg.recvMsg(serverSocket);
                if (request == null) {
                    logger.error("interrupted");
                    break;
                }
                logger.info("Received Request Msg ");
                for (ZFrame frame : request) {
                    logger.info("Frame: {}", frame.toString());
                }
                ZMsg response = new ZMsg();
                response.add("Hello from the other side");
                response.send(serverSocket);
            }
        } catch (Exception e) {
            logger.error("mq component failed", e);
        } finally {
            context.close();
        }

    }
}
