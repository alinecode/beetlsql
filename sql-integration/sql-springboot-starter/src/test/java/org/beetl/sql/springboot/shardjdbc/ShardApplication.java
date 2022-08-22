package org.beetl.sql.springboot.shardjdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 使用主从库，更新走主库，查询走从库
 */
@PropertySource(value = {
        "classpath:application-shard.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class ShardApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShardApplication.class, args);
    }

}
