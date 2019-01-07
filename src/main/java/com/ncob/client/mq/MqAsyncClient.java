package com.ncob.client.mq;

import com.ncob.common.mq.MqComponent;
import com.ncob.common.util.cli.NetworkingCli;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MqAsyncClient extends MqComponent {

    public MqAsyncClient(NetworkingCli networkingCli, ExecutorService executorService, ZContext zContext) {
        super(networkingCli.getHost(), networkingCli.getPort(), executorService, zContext);
    }

    public static void main(String[] args) throws ParseException {
        NetworkingCli networkingCli = new NetworkingCli("client", args);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        MqAsyncClient client = new MqAsyncClient(networkingCli, executorService, new ZContext(1));
        client.start();
    }

    @Override
    public void run() {
        String address = getAddress();
        log.info("Connecting client to {}", address);
        ZContext context = new ZContext(1);
        try {
            ZMQ.Socket clientSocket = context.createSocket(ZMQ.DEALER);//DEALER socket can receive and send in any order

            clientSocket.connect(address);
            log.info("Client started");
            String testMessage = "UserId1";
            ZMsg msg = new ZMsg();
            msg.add(testMessage);
            msg.send(clientSocket);
            log.info("Sent Msg");
            while (true) {
                ZMsg response = ZMsg.recvMsg(clientSocket);
                log.info("Received Response Msg {}", response);
            }
        } catch (Exception e) {
            log.error("mq component failed", e);
        } finally {
            context.close();
        }
    }
}
