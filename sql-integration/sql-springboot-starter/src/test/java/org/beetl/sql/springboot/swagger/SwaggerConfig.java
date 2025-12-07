package org.beetl.sql.springboot.swagger;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.JsonNode;
import org.beetl.sql.starter.CodeGenController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@ConditionalOnClass(Docket.class)
public class SwaggerConfig {

	@Bean
	public Docket docketApi() {
		TypeResolver typeResolver = new TypeResolver();
		final ResolvedType jsonNodeType = typeResolver.resolve(JsonNode.class);
		final ResolvedType stringType = typeResolver.resolve(String.class);

		Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName("just test").apiInfo(apiInfo())
				.alternateTypeRules(new AlternateTypeRule(jsonNodeType, stringType)).select().build()
				.enableUrlTemplating(false);
		;
		return docket;

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("swagger beetlsql test").description("当swagger存在的时候，beetlsql自动加入代码生成部分")
				.contact(new Contact("xiandafu", "", "xiandafu@126.com")).license("测试").licenseUrl("测试url")
				.version("1.0").build();
	}



	@Bean
	public CodeGenController codeGenController() {
		return new CodeGenController();
	}


}
