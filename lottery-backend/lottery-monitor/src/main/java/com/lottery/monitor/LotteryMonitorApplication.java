package com.lottery.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lottery")
public class LotteryMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LotteryMonitorApplication.class, args);
    }
}
