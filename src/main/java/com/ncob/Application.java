package com.ncob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zeromq.ZContext;

@SpringBootApplication
@Slf4j
@Configuration
@EnableMongoRepositories
@PropertySource("config/mq.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("Server started");
    }

    @Bean
    public ThreadPoolExecutorFactoryBean getExecutorService() {
        ThreadPoolExecutorFactoryBean threadPoolExecutorFactoryBean = new ThreadPoolExecutorFactoryBean();
        threadPoolExecutorFactoryBean.setCorePoolSize(3);
        threadPoolExecutorFactoryBean.setMaxPoolSize(6);
        threadPoolExecutorFactoryBean.setQueueCapacity(5);
        return threadPoolExecutorFactoryBean;
        /*
        Take this example. Starting thread pool size is 1, core pool size is 5, max pool size is 10 and the queue is 100. As requests come in, threads will be created up to 5 and then tasks will be added to
        the queue until it reaches 100. When the queue is full new threads will be created up to maxPoolSize. Once all the threads are in use and the queue is full tasks will be rejected. As the queue
        reduces, so does the number of active threads.
         */
    }

    @Bean
    public ZContext getZContext() {
        return new ZContext(1);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}