package hello;

import org.zeromq.ZContext;
import server.mq.MqRouter;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import common.util.cli.NetworkingCli;

@SpringBootApplication
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);
    private static NetworkingCli routerCli;


    public static void main(String[] args) throws ParseException {
        routerCli = new NetworkingCli("router", args);
        SpringApplication.run(Application.class, args);
        logger.info("Server started");

        // Add a simple protobuf implementation here

    }

    @Bean
    public ThreadPoolExecutorFactoryBean getExecutorService() {
        return new ThreadPoolExecutorFactoryBean();
    }

    @Bean
    public ZContext getZContext() {
        return new ZContext(1);
    }

    @Bean
    public MqRouter getMqRouter() {
        return new MqRouter(routerCli);
    }

}