package com.ncob.common.mq;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.zeromq.ZContext;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MqComponent implements Runnable {
    private final @NonNull String host;
    private final @NonNull String port;
    private final  @NonNull ExecutorService executor;
    protected final @NonNull ZContext context;

    @PostConstruct
    public void start() {
        executor.execute(this);
    }

    protected String getAddress() {
        return "tcp://" + host + ":" + port;
    }

    protected String getInprocAddress() {
        return "inproc://backend";
    }
}
