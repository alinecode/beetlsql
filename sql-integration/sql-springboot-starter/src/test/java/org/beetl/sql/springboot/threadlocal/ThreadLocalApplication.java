package org.beetl.sql.springboot.threadlocal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 使用一个sqlManager，代理多个数据源，通过@Use注解指示使用哪个数据源
 */
@PropertySource(value = {
        "classpath:application-threadlocal.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class ThreadLocalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThreadLocalApplication.class, args);
    }

}