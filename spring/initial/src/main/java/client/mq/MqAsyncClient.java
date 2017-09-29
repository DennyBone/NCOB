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

public class MqAsyncClient extends MqComponent {
    private static final Logger logger = LoggerFactory.getLogger(MqClient.class);

    public MqAsyncClient(NetworkingCli networkingCli) {
        super(networkingCli);
    }

    public static void main(String[] args) throws ParseException {
        NetworkingCli networkingCli = new NetworkingCli("client", args);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        MqAsyncClient client = new MqAsyncClient(networkingCli);
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
            ZMQ.Socket clientSocket = context.createSocket(ZMQ.DEALER);//DEALER socket can receive and send in any order

            clientSocket.connect(address);
            logger.info("Client started");
            String testMessage = "UserId1";
            ZMsg msg = new ZMsg();
            msg.add(testMessage);
            msg.send(clientSocket);
            logger.info("Sent Msg");
            while (true) {
                ZMsg response = ZMsg.recvMsg(clientSocket);
                logger.info("Received Response Msg {}", response);
            }
        } catch (Exception e) {
            logger.error("mq component failed", e);
        } finally {
            context.close();
        }
    }
}
