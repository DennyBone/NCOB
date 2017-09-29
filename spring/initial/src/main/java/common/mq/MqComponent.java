package common.mq;

import common.util.cli.NetworkingCli;
import org.springframework.beans.factory.annotation.Autowired;
import org.zeromq.ZContext;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

public abstract class MqComponent implements Runnable {
    private final NetworkingCli networkingCli;
    private ExecutorService executor;
    protected ZContext context;

    public MqComponent(NetworkingCli networkingCli) {
        this.networkingCli = networkingCli;
    }

    @Autowired
    public void setExecutor(ExecutorService executorService) {
        this.executor = executorService;
    }

    @Autowired
    public void setZContext(ZContext context) {
        this.context = context;
    }

    @PostConstruct
    public void start() {
        executor.execute(this);
    }

    public String getBindingAddress() {
        return "tcp://" + networkingCli.getBindingHost() + ":" + networkingCli.getBindingPort();
    }

    public String getTargetAddress() {
        return "tcp://" + networkingCli.getTargetHost() + ":" + networkingCli.getTargetPort();
    }
}
