package com.beetl.sql.encrypt.test;



import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

public class EncryptTest {
	static DataSource dataSource = datasource();

	private static DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");

		return ds;
	}

	private static SQLManager getSQLManager() {

		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		builder.setProduct(false);
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}


	@Test
	public void testEncrypt(){
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/encrypt-schema.sql");
		MyTestLog log = new MyTestLog();
		log.setCode("a");
		log.setContent("c");
		sqlManager.insert(log);

		log = sqlManager.unique(MyTestLog.class,log.getId());
		System.out.println(log);

		MyTestLog2 log2 = new MyTestLog2();
		log2.setCode("a");
		log2.setContent("c");
		sqlManager.insert(log2);

		log2 = sqlManager.unique(MyTestLog2.class,log2.getId());
		System.out.println(log2);



	}




}
