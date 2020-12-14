package org.beetl.sql.saga.demorun;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@PropertySource(value = {
		"classpath:common-application.properties","classpath:user-application.properties"
}, encoding = "utf-8")
@SpringBootApplication(scanBasePackages = {"org.beetl.sql.saga.demo", "org.beetl.sql.saga.ms.client"})
@EnableKafka
public class UserApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}
}