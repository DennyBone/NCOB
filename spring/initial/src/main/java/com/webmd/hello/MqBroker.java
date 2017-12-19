package com.webmd.hello;

import com.webmd.common.mq.MqComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.util.concurrent.ExecutorService;

/**
 * Connect 'frontend' clients to 'backend' workers
 */
@Component
@Slf4j
public class MqBroker extends MqComponent
{

    @Autowired
    public MqBroker(@Value("${mq.router.host}") String host, @Value("${mq.router.port}") String port, ExecutorService executor, ZContext context)
    {
        super(host, port, executor, context);
    }

    @Override
    public void run()
    {
        final String frontendAddress = getAddress(); // "tcp://*:61615"
        final String backendAddress = "tcp://*:61616";
        log.info("Hello from Broker");
//
//        //  Prepare our context and sockets
//        Context context = ZMQ.context(1);
//
//        //  Socket facing clients
//        Socket frontend = context.socket(ZMQ.ROUTER);
//        frontend.bind(frontendAddress);
//
//        //  Socket facing services
//        Socket backend = context.socket(ZMQ.DEALER);
//        backend.bind(backendAddress);
//
//        //  Start the proxy
//        log.info("starting broker");
//        ZMQ.proxy(frontend, backend, null);
//
//        //  We never get here but clean up anyhow
//        frontend.close();
//        backend.close();
//        context.term();
    }

}
