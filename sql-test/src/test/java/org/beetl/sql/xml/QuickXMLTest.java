package org.beetl.sql.xml;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.entity.User;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class QuickXMLTest {
	static HikariDataSource dataSource;
	static SQLManager sqlManager;
	@BeforeClass
	public static void start() {
		datasource();
		initSQLManager();
	}


	@Test
	public void testXMLLoader(){
		XML.support(sqlManager);
		List<User> list = sqlManager.select(SqlId.of("user.select"),User.class);
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
