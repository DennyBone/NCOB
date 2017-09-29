package hello;

import mq.MqServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

@SpringBootApplication
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("Server started");

        // Add a simple protobuf implementation here

    }

    @Bean
    public ThreadPoolExecutorFactoryBean getExecutorService() {
        return new ThreadPoolExecutorFactoryBean();
    }

    @Bean
    public MqServer getMqServer() {
        return new MqServer();
    }

}