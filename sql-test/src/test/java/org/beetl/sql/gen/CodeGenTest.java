package org.beetl.sql.gen;

import org.beetl.sql.BaseTest;
import org.beetl.sql.gen.simple.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试SQLmanager获取表信息并转化为 {@link org.beetl.sql.gen.Entity}
 */
public class CodeGenTest extends BaseTest {

	@BeforeClass
	public static void init(){
		initTable(testSqlFile);
	}


	@Test
	public void buildDoc(){
		List<SourceBuilder> sourceBuilder = new ArrayList<>();

		SourceBuilder docBuilder = new MDDocBuilder();
		sourceBuilder.add(docBuilder);

		SourceConfig config = new SourceConfig(sqlManager,sourceBuilder);

		StringOnlyProject project = new StringOnlyProject();
		String tableName = "sys_user";
		config.gen(tableName,project);
		String content = project.getContent();
		Assert.assertTrue(content.contains("| 名称 | 数据类型 | 长度  |  说明 |"));

	}

	@Test
	public void buildEntityByTableName(){
		List<SourceBuilder> sourceBuilder = new ArrayList<>();
		SourceBuilder entityBuilder = new EntitySourceBuilder();
		SourceBuilder entityBuilder2 = new EntitySourceBuilder(true);
		SourceBuilder mapperBuilder = new MapperSourceBuilder();
		SourceBuilder mdBuilder = new MDSourceBuilder();
		SourceBuilder docBuilder = new MDDocBuilder();

		sourceBuilder.add(entityBuilder);
		sourceBuilder.add(mapperBuilder);
		sourceBuilder.add(mdBuilder);
		sourceBuilder.add(docBuilder);

		SourceConfig config = new SourceConfig(sqlManager,sourceBuilder);

		BaseProject project = new ConsoleOnlyProject();
   		String tableName = "sys_user";
   		config.gen(tableName,project);
	}
}
