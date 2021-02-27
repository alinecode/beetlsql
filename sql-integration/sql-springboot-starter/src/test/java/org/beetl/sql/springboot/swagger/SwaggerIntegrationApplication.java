package org.beetl.sql.springboot.swagger;

import org.beetl.sql.springboot.datasource2.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@PropertySource(value = {
		"classpath:swagger-application.yml"
}, encoding = "utf-8",factory = YamlPropertySourceFactory.class)
@SpringBootApplication
@RestController
public class SwaggerIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwaggerIntegrationApplication.class, args);

	}

	@GetMapping("/hello")
	public String sayHello(String name){
		return "hello "+name;
	}

}
