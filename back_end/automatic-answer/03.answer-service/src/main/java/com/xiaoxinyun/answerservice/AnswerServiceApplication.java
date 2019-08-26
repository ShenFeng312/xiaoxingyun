package com.xiaoxinyun.answerservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = "com.xiaoxinyun")
@EnableDubbo
@MapperScan(basePackages = "com.xiaoxinyun.answerservice.repository.mapper")
public class AnswerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnswerServiceApplication.class, args);
    }

}
