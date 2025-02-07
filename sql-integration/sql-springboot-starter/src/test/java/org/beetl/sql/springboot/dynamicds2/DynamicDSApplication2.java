package org.beetl.sql.springboot.dynamicds2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 动态创建数据源的例子
 * @see DynamicDSService2
 */
@PropertySource(value = {
	"classpath:application-dynamic-ds-2.properties"
}, encoding = "utf-8")
@SpringBootApplication
public class DynamicDSApplication2 {
	public static void main(String[] args) {
		SpringApplication.run(DynamicDSApplication2.class, args);

	}

}
