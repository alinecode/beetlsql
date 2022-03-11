package org.beetl.sql.springboot.masterslave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 使用主从库，更新走主库，查询走从库
 */
@PropertySource(value = {
        "classpath:application-master-slave.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class MasterSlaveApplication {
    public static void main(String[] args) {
        SpringApplication.run(MasterSlaveApplication.class, args);
    }

}