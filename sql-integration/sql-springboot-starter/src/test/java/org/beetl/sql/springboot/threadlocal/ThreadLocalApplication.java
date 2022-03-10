package org.beetl.sql.springboot.threadlocal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {
        "classpath:application-threadlocal.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class ThreadLocalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThreadLocalApplication.class, args);
    }

}