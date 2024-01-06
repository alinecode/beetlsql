package org.beetl.sql.meta;

import org.beetl.core.Template;
import org.beetl.sql.BaseTest;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.BeetlSQLTemplateEngine;

import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.engine.template.SQLTemplate;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaTest extends BaseTest {
	@BeforeClass
	public static void init(){
		initTable(testSqlFile);
	}

	@Test
	public void findMeta(){
		// select * from sys_user  where id =#{id} and name = #{name} and age=#{other.cc}
		SqlId sqlId = SqlId.of("user.metaGet");
		BeetlTemplateEngine beetlSQLTemplateGroup
			= (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
		Template template = beetlSQLTemplateGroup.getBeetl().getGroupTemplate().getTemplate(sqlId);
		Collection collection = template.program.metaData.globalIndexMap.keySet();
		System.out.println(collection);
	}
}
