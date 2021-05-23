package org.beetlsql.sql.saga.test.spring;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot集成测试
 */
@SpringBootApplication
public class SpringSagaLocalApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSagaLocalApplication.class, args);
    }
}