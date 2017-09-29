package server.mq;

import common.mq.MqComponent;
import common.util.cli.NetworkingCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class MqRepSocket extends MqComponent {
    private static final Logger logger = LoggerFactory.getLogger(MqRepSocket.class);

    public MqRepSocket(NetworkingCli networkingCli) {
        super(networkingCli);
    }

    @Override
    public void run() {
        final String address = getBindingAddress();
        try {
            ZMQ.Socket serverSocket = context.createSocket(ZMQ.REP);

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
