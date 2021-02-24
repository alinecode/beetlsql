package org.beetl.sql.springboot.datasource2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {
        "classpath:application-two-db.yml"
}, encoding = "utf-8",factory =YamlPropertySourceFactory.class)
@SpringBootApplication
public class TwoDBApplication {
    public static void main(String[] args) {
        SpringApplication.run(TwoDBApplication.class, args);
    }

}