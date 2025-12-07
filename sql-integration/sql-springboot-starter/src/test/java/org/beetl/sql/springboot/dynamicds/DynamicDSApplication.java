package org.beetl.sql.springboot.dynamicds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 动态创建数据源的例子
 * @see DynamicDSService
 */
@PropertySource(value = {
	"classpath:application-dynamic-ds.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class DynamicDSApplication {
	public static void main(String[] args) {
		SpringApplication.run(DynamicDSApplication.class, args);

	}

}
