package com.adbroker.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdManagerApplication.class, args);
    }
}