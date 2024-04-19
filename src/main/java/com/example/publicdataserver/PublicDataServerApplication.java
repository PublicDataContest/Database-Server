package com.example.publicdataserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PublicDataServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublicDataServerApplication.class, args);
    }

}
