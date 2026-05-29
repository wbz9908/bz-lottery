package com.lottery.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lottery")
public class LotteryPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LotteryPayApplication.class, args);
    }
}
