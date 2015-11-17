package com.test.vote.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration

public class VoteServiceStarter{
    private static final Logger LOGGER = LoggerFactory.getLogger(VoteServiceStarter.class);

    public static void main(String args[]) {

        SpringApplication.run(VoteServiceStarter.class, args);
    }

}
