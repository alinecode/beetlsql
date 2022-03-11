package org.beetl.sql.springboot.dynamic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 数据源的选择依赖POJO上的注解
 */
@PropertySource(value = {
        "classpath:application-dynamic.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class DynamicApplication {
    public static void main(String[] args) {
        SpringApplication.run(DynamicApplication.class, args);
    }

}