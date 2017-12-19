package com.webmd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.zeromq.ZContext;

@SpringBootApplication
@Slf4j
@Configuration
@PropertySource("config/mq.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("Server started");
    }

    @Bean
    public ThreadPoolExecutorFactoryBean getExecutorService() {
        ThreadPoolExecutorFactoryBean threadPoolExecutorFactoryBean = new ThreadPoolExecutorFactoryBean();
        threadPoolExecutorFactoryBean.setCorePoolSize(4);
        return threadPoolExecutorFactoryBean;
    }

    @Bean
    public ZContext getZContext() {
        return new ZContext(1);
    }

}