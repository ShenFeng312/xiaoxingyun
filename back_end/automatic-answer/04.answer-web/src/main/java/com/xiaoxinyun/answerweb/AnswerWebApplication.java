package com.xiaoxinyun.answerweb;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.xiaoxinyun")
@EnableDubbo
public class AnswerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnswerWebApplication.class, args);
    }

}
