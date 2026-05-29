package com.lottery.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lottery")
public class LotteryActivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(LotteryActivityApplication.class, args);
    }
}
