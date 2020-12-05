package org.beetl.sql.saga.ms.server.config;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * saga swagger
 * @author xiandafu
 */
@Configuration
public class SwaggerConfig {

	private String apiPathRegex = "/api.*";
    @Bean
    public Docket sagaApi() {
        TypeResolver typeResolver = new TypeResolver();
        final ResolvedType jsonNodeType =
                typeResolver.resolve(
                        JsonNode.class);
        final ResolvedType stringType =
                typeResolver.resolve(
                        String.class);

        Docket docket  =  new Docket(DocumentationType.SWAGGER_2)
                .groupName("saga")
                .apiInfo(apiInfo())
                .alternateTypeRules(
                        new AlternateTypeRule(
                                jsonNodeType,
                                stringType))
                .select()
				.paths(apiPaths())
                .build().enableUrlTemplating(false);;

        return docket;

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Saga Server")
                .description("Saga Server")
                .contact(new Contact("beetlsql", "", "xiandafu@126.com"))
                .license("FreeBSD")
                .licenseUrl("FreeBSD")
                .version("1.0")
                .build();
    }

	private Predicate<String> apiPaths() {
		return regex(apiPathRegex);
	}


}