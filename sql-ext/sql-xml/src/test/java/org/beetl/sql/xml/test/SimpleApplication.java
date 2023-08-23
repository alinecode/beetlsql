package org.beetl.sql.xml.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {
        "classpath:application-simple.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class SimpleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleApplication.class, args);
    }

}
