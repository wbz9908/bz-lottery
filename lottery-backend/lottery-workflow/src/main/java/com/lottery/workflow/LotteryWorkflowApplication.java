package com.lottery.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lottery")
public class LotteryWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(LotteryWorkflowApplication.class, args);
    }
}
