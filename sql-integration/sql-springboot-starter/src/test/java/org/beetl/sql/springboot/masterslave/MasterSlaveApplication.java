package org.beetl.sql.springboot.masterslave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {
        "classpath:application-master-slave.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class MasterSlaveApplication {
    public static void main(String[] args) {
        SpringApplication.run(MasterSlaveApplication.class, args);
    }

}