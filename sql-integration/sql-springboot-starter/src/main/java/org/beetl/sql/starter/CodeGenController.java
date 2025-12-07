package org.beetl.sql.starter;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.gen.SourceBuilder;
import org.beetl.sql.gen.SourceConfig;
import org.beetl.sql.gen.simple.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个方便工具类，使用者可以在集成swagger基础上，增加一个代码生成api
 * <pre>{@code
 * @RestController
 * public class MyCodeGenController extends CodeGenController{}
 *
 *
 * }</pre>
 * 如上类，确保MyCodeGenController能被springboot扫描到，则swagger api自动增加一个代码生成&预览的api
 */
@RestController
public class CodeGenController {

	  @Autowired
	  protected SQLManager sqlManager;

	@ApiOperation(notes = "预览", value = "预览生成的代码")
	@GetMapping("/beetlsql3/preview")
	public String sayHello(@ApiParam(value = "表名", required = true) String tableName) {
		SourceConfig config = new SourceConfig(sqlManager, getSourceBuilder());
		StringOnlyProject project = new StringOnlyProject();
		config.gen(tableName, project);
		return project.getContent();
	}

	@PostMapping("/beetlsql3/gen")
	@ApiOperation(notes = "代码生成", value = "代码生成到目标路径")
	public String genAll(@ApiParam(value = "表名", required = true) @RequestParam String tableName,
			@ApiParam(value = "包名,默认是com.test", required = true) @RequestParam String pkg,
			@ApiParam(value = "目标路径，默认是工程根目录", required = true) @RequestParam String sourceRoot) {
		SimpleMavenProject simpleMavenProject = new SimpleMavenProject();
		if (!StringKit.isBlank(sourceRoot)) {
			simpleMavenProject.setRoot(sourceRoot);
		}
		if (!StringKit.isBlank(pkg)) {
			simpleMavenProject.setBasePackage(pkg);
		}

		SourceConfig config = new SourceConfig(sqlManager, getSourceBuilder());

		config.gen(tableName, simpleMavenProject);

		return "成功，生成目录:" + simpleMavenProject.getRoot();
	}

	@PostMapping("/beetlsql3/genAll")
	@ApiOperation(notes = "所有表的代码生成，请谨慎使用，以免覆盖", value = "代码生成到目标路径")
	public String genAll(
			@ApiParam(value = "包名,默认是com.test", required = false) @RequestParam String pkg,
			@ApiParam(value = "目标路径，默认是工程根目录", required = false) @RequestParam String sourceRoot) {
		SimpleMavenProject simpleMavenProject = new SimpleMavenProject();
		if (!StringKit.isBlank(sourceRoot)) {
			simpleMavenProject.setRoot(sourceRoot);
		}
		if (!StringKit.isBlank(pkg)) {
			simpleMavenProject.setBasePackage(pkg);
		}

		SourceConfig config = new SourceConfig(sqlManager, getSourceBuilder());
		config.genAll(simpleMavenProject);
		return "成功，生成目录:" + simpleMavenProject.getRoot();
	}

	@ApiOperation(notes = "查看生成的目标路径", value = "查看生成的目标路径")
	@GetMapping("/beetlsql/root")
	public String root() {
		SimpleMavenProject simpleMavenProject = new SimpleMavenProject();
		return "代码生成到:" + simpleMavenProject.getRoot();
	}

	protected List<SourceBuilder> getSourceBuilder() {
		List<SourceBuilder> sourceBuilder = new ArrayList<>();
		SourceBuilder entityBuilder = new EntitySourceBuilder();
		SourceBuilder mapperBuilder = new MapperSourceBuilder();
		SourceBuilder mdBuilder = new MDSourceBuilder();
		MDDocBuilder mdDocBuilder = new MDDocBuilder();

		sourceBuilder.add(entityBuilder);
		sourceBuilder.add(mapperBuilder);
		sourceBuilder.add(mdBuilder);
		sourceBuilder.add(mdDocBuilder);
		return sourceBuilder;
	}


}
