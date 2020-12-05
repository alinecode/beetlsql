package org.beetl.sql.saga.ms.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SagaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SagaServerApplication.class, args);
	}

}
