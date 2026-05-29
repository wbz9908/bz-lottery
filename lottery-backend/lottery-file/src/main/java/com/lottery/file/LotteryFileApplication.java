package com.lottery.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lottery")
public class LotteryFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(LotteryFileApplication.class, args);
    }
}
