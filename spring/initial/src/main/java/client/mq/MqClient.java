package client.mq;

import common.mq.MqComponent;
import common.util.cli.NetworkingCli;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MqClient extends MqComponent {
    private static final Logger logger = LoggerFactory.getLogger(MqClient.class);

    MqClient(NetworkingCli networkingCli) {
        super(networkingCli);
    }


    public static void main(String[] args) throws ParseException {
        NetworkingCli networkingCli = new NetworkingCli("client", args);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        MqClient client = new MqClient(networkingCli);
        client.setExecutor(executorService);
        client.setZContext(new ZContext(1));
        client.start();
    }

    @Override
    public void run() {
        String address = getTargetAddress();
        logger.info("Connecting client to {}", address);
        ZContext context = new ZContext(1);
        try {
            org.zeromq.ZMQ.Socket clientSocket = context.createSocket(ZMQ.REQ);//REQ socket can only send and receive in lock step

            clientSocket.connect(address);
            logger.info("Client started");
            for (int i = 0; i < 10; ++i) {
                ZMsg zMsg = new ZMsg();
                zMsg.add("Hello");
                zMsg.send(clientSocket);//Will pre-pend a null frame
                ZMsg response = ZMsg.recvMsg(clientSocket);
                logger.info("Received Response Msg {}", response);
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            logger.error("mq component failed", e);
        } finally {
            context.close();
        }

    }
}
