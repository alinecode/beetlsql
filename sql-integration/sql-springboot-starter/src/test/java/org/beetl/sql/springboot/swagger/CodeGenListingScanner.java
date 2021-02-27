package org.beetl.sql.springboot.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.*;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

@SuppressWarnings({
		"WhitespaceAround",
		"ParenPad",
		"deprecation"})
public class CodeGenListingScanner implements ApiListingScannerPlugin {

	// tag::api-listing-plugin[]
	private final CachingOperationNameGenerator operationNames;

	/**
	 * @param operationNames - CachingOperationNameGenerator is a component bean
	 *                       that is available to be autowired
	 */
	public CodeGenListingScanner(CachingOperationNameGenerator operationNames) {//<9>
		this.operationNames = operationNames;
	}

	@Override
	public List<ApiDescription> apply(DocumentationContext context) {
		return new ArrayList<>(
				Arrays.asList( //<1>
						new ApiDescription(
								"gen",
								"/beetlsql/show",
								"代码生成",
								"代码生成，生成entity，mapper，markdown，doc",
								Collections.singletonList( //<2>
										new OperationBuilder(operationNames)
												.authorizations(new ArrayList<>())
//												.codegenMethodNameStem("bug1767GET") //<3>
												.method(HttpMethod.GET)
												.notes("生成代码到控制台")
												.parameters(
														Collections.singletonList( //<4>
																new springfox.documentation.builders.ParameterBuilder()
																		.type(new TypeResolver()
																				.resolve(String.class))
																		.name("description")
																		.parameterType("query")
																		.parameterAccess("access")
																		.required(true)
																		.modelRef(new springfox.documentation.schema.ModelRef(
																				"string")) //<5>
																		.build()))
												.requestParameters(
														Collections.singletonList( //<4a>
																new RequestParameterBuilder()
																		.description("table name")
																		.name("table")
																		.required(true)
																		.in(ParameterType.QUERY)
																		.query(q -> q.model(m -> m.scalarModel(ScalarType.STRING))) //<5b>
																		.build()))
												.responses(responseMessages()) //<6>
												.responseModel(new springfox.documentation.schema.ModelRef("string")) //<7>
												.responses(responses()) //<6b>
												.build()),
								false)));
	}

	/**
	 * @return Set of response messages that overide the default/global response messages
	 */
	private Set<Response> responseMessages() { //<8>
		return singleton(new ResponseBuilder()
				.code("200")
				.description("代码生成成功")
				.representation(MediaType.TEXT_PLAIN)
				.apply(r -> r.model(m -> m.scalarModel(ScalarType.STRING)))
				.build());
	}

	/**
	 * @return Set of response messages that overide the default/global response messages
	 */
	private Collection<Response> responses() { //<8b>
		return singletonList(new ResponseBuilder()
				.code("200")
				.description("代码生成成功")
				.representation(MediaType.ALL)
				.apply(r -> r.model(m -> m.scalarModel(ScalarType.STRING))
						.build())
				.build());
	}
	// end::api-listing-plugin[]

	@Override
	public boolean supports(DocumentationType delimiter) {
		return DocumentationType.SWAGGER_2.equals(delimiter);
	}

}