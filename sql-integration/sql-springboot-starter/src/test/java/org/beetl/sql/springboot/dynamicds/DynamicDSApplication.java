package org.beetl.sql.springboot.dynamicds;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.function.Supplier;

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
