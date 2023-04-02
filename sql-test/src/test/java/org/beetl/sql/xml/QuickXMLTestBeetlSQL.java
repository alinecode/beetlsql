package org.beetl.sql.xml;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.entity.User;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickXMLTestBeetlSQL {
	static HikariDataSource dataSource;
	static SQLManager sqlManager;
	@BeforeClass
	public static void start() {
		datasource();
		initSQLManager();
		XMLBeetlSQL.support(sqlManager);
	}


	@Test
	public void testIf(){
		User user = new User();
		user.setName("lijz");
		List<User> list = sqlManager.select(SqlId.of("user.testIf"),User.class,user);
		Assert.assertEquals("lijz",list.get(0).getName());

	}

	@Test
	public void testIsNotEmpty(){

		User user = new User();
		user.setName("lijz");
		List<User> list = sqlManager.select(SqlId.of("user.testIsNotEmpty"),User.class,user);
		Assert.assertEquals("lijz",list.get(0).getName());

	}

	@Test
	public void testIsBlank(){
		User user = new User();
		user.setName("");
		List<User> list = sqlManager.select(SqlId.of("user.testIsBlank"),User.class,user);
		Assert.assertTrue(list.size()==1);

	}

	@Test
	public void testForeach(){
		Map map = new HashMap();
		map.put("ids", Arrays.asList(1,2));
		List<User> list = sqlManager.select(SqlId.of("user.testForeach"),User.class,map);
		Assert.assertTrue(list.size()==2);

	}

	@Test
	public void testInclude(){
		Map map = new HashMap();
		map.put("id", 1);
		List<User> list = sqlManager.select(SqlId.of("user.testInclude"),User.class,map);
		Assert.assertTrue(list.size()==1);

	}

	@Test
	public void testWhere(){
		List<User> list = sqlManager.select(SqlId.of("user.testWhere"),User.class);
		Assert.assertTrue(list.size()>0);

	}


	@Test
	public void testTrim(){
		List<User> list = sqlManager.select(SqlId.of("user.testTrim"),User.class);
		Assert.assertTrue(list.size()==1);

	}

	@Test
	public void testBind(){
		Map map = new HashMap();
		map.put("id", 0);
		List<User> list = sqlManager.select(SqlId.of("user.testBind"),User.class,map);
		Assert.assertEquals("lijz",list.get(0).getName());

	}

	@Test
	public void testAll(){

		List<User> list = sqlManager.select(SqlId.of("user.testAll"),User.class);
		Assert.assertTrue(list.size()==0);

	}


	public static void datasource() {
		dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setMaximumPoolSize(5);

	}

	public static void initSQLManager() {
		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setDbStyle(new H2Style());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setSqlLoader(new XMLClasspathLoader("sql"));
//		builder.setInters(new Interceptor[]{new SimpleDebugInterceptor()});
		// slf4j-beetlsql-log
//		builder.setInters(new Interceptor[]{new Slf4JLogInterceptor()});
		sqlManager = builder.build();
		DBInitHelper.executeSqlScript(sqlManager,"db/db-init.sql");
	}


}
