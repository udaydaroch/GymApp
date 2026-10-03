package com.example.rival;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RivalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RivalApplication.class, args);
    }
}
