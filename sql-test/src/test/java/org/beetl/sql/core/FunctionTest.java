package org.beetl.sql.core;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.BaseTest;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FunctionTest extends BaseTest {
	@BeforeClass
	public static void init() {
		initTable(testSqlFile);
	}

	@Test
	public void useTest() {
		//use
		SqlId id = SqlId.of("function.useTest");
		Map map = Params.ins().add("id",1).map();
		try{
			List<Map> list = sqlManager.select(id,Map.class,map);
		}catch(Exception ex){
			Assert.fail();
		}

		//globalUse
		id = SqlId.of("function.useTest2");
		map = Params.ins().add("id",1).map();
		try{
			List<Map> list = sqlManager.select(id,Map.class,map);
		}catch(Exception ex){
			Assert.fail();
		}

	}

	@Test
	public void whereTest() {
		//where
		SqlId id = SqlId.of("function.whereTest");
		Map map = Params.ins().add("id",1).map();
		try{
			List<Map> list = sqlManager.select(id,Map.class,map);
		}catch(Exception ex){
			ex.printStackTrace();
			Assert.fail();
		}

	}


	@Test
	public void trimTest() {
		//trim
		SqlId id = SqlId.of("function.trimTest");
		Map map = Params.ins().add("id",1).map();
		try{
			List<Map> list = sqlManager.select(id,Map.class,map);
		}catch(Exception ex){
			ex.printStackTrace();
			Assert.fail();
		}

	}

	@Test
	public void joinTest() {
		//join
		SqlId id = SqlId.of("function.joinTest");
		Map map = Params.ins().add("ids", Arrays.asList(1,2)).map();
		try{
			List<Map> list = sqlManager.select(id,Map.class,map);
			Assert.assertEquals(2,list.size());
		}catch(Exception ex){
			ex.printStackTrace();
			Assert.fail();
		}

	}

	@Test
	public void registerFunctionTest() {
		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
		beetlTemplateEngine.getBeetl().getGroupTemplate().registerFunction("now", new Function() {
			@Override
			public Object call(Object[] paras, Context ctx) {
				return new java.util.Date();
			}
		});

		String sql = "select * from sys_user where create_date<#{now()}";

		Map map = Params.ins().add("ids", 1).map();
		try{
			List<Map> list = sqlManager.execute(sql,Map.class,map);
		}catch(Exception ex){
			ex.printStackTrace();
			Assert.fail();
		}

	}
}