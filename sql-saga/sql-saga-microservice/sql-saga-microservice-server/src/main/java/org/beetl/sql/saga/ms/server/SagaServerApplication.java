package org.beetl.sql.saga.ms.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
@PropertySource({""})
@PropertySource(value = {
		"classpath:saga-server-beetlsql-application.properties"
}, encoding = "utf-8")
@SpringBootApplication
@EnableKafka
public class SagaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SagaServerApplication.class, args);
	}

}
