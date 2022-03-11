package org.beetl.sql.springboot.datasource2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 一个应用里使用俩个数据源
 */
@PropertySource(value = {
        "classpath:application-two-db.yml"
}, encoding = "utf-8",factory =YamlPropertySourceFactory.class)
@SpringBootApplication
public class TwoDBApplication {
    public static void main(String[] args) {
        SpringApplication.run(TwoDBApplication.class, args);
    }

}