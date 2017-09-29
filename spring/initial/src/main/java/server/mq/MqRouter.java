package server.mq;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import common.mq.MqComponent;
import common.util.cli.NetworkingCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.HashSet;

@ManagedResource
public class MqRouter extends MqComponent {
    private static final Logger logger = LoggerFactory.getLogger(MqRouter.class);
    private final BiMap<byte[], String> connectionIdToClientId = HashBiMap.create();
    private ZMQ.Socket routerSocket;


    public MqRouter(NetworkingCli networkingCli) {
        super(networkingCli);
    }

    @Override
    public void run() {
        final String address = getBindingAddress();
        try {
            routerSocket = context.createSocket(ZMQ.ROUTER);
            routerSocket.bind(address);
            routerSocket.setRouterMandatory(true);//let us know if connection id does not exist
            logger.info("Mq router port bound to {}", address);
            while (true) {
                ZMsg request = ZMsg.recvMsg(routerSocket);
                if (request == null) {
                    logger.error("interrupted");
                    break;
                }
                logger.debug("Received Msg {}", request);

                //router pre-pends unique id in form of 5 bytes for client connections
                byte[] connectionId = request.poll().getData();//IMPORTANT router expects key in 5 bytes when sending, do NOT read key as string
                String nullFrame = request.poll().toString();//Annoyingly REQ socket will place null frame before message, DEALER will not
                String clientId;
                if (nullFrame.equals("")) {
                    clientId = request.poll().toString();
                } else {
                    clientId = nullFrame;
                }
                connectionIdToClientId.put(connectionId, clientId);
                sendMessage("Hello from the other side", clientId);
            }
        } catch (Exception e) {
            logger.error("mq component failed", e);
        } finally {
            context.close();
        }
    }

    @ManagedOperation
    public HashSet<String> getConnectedClientIds() {
        return new HashSet<>(connectionIdToClientId.values());
    }

    @ManagedOperation
    public void sendMessage(String message, String clientId) {
        byte[] connectionId = connectionIdToClientId.inverse().get(clientId);
        if (connectionId == null) {
            throw new IllegalArgumentException("No client connection for client " + clientId);
        }
        logger.debug("Sending message: {}", message);
        ZMsg zMsg = new ZMsg();
        zMsg.add(connectionId);
        zMsg.add("");//add null frame for REQ socket as required, assume implementation of a DEALER socket is smart enough to check for it.
        zMsg.add(message);
        boolean success = zMsg.send(routerSocket);//strips connection id from first frame and routes message accordingly
        if (!success) {
            logger.error("Send to client {} unsuccessful", connectionId);
        }
    }

}
