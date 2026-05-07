package com.myptai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MyPtAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPtAiApplication.class, args);
    }
}
