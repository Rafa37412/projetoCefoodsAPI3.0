package com.projetocefoods.cefoods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CefoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CefoodsApplication.class, args);
    }
}