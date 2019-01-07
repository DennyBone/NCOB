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
public class MqClient extends MqComponent {

    private MqClient(NetworkingCli networkingCli, ZContext zContext, ExecutorService executorService) {
        super(networkingCli.getHost(),networkingCli.getHost(), executorService, zContext);
    }


    public static void main(String[] args) throws ParseException {
        NetworkingCli networkingCli = new NetworkingCli("client", args);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        MqClient client = new MqClient(networkingCli, new ZContext(1), executorService);
        client.start();
    }

    @Override
    public void run() {
        String address = getAddress();
        log.info("Connecting client to {}", address);
        ZContext context = new ZContext(1);
        try {
            org.zeromq.ZMQ.Socket clientSocket = context.createSocket(ZMQ.REQ);//REQ socket can only send and receive in lock step

            clientSocket.connect(address);
            log.info("Client started");
            for (int i = 0; i < 10; ++i) {
                ZMsg zMsg = new ZMsg();
                zMsg.add("Hello");
                zMsg.send(clientSocket);//Will pre-pend a null frame
                ZMsg response = ZMsg.recvMsg(clientSocket);
                log.info("Received Response Msg {}", response);
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            log.error("mq component failed", e);
        } finally {
            context.close();
        }

    }
}
