package org.beetl.sql.test;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.ReThrowConsoleErrorHandler;
import org.beetl.sql.clazz.ClassAnnotation;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.nosql.TaosStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.gen.SourceBuilder;
import org.beetl.sql.gen.SourceConfig;
import org.beetl.sql.gen.simple.ConsoleOnlyProject;
import org.beetl.sql.gen.simple.EntitySourceBuilder;
import org.beetl.sql.gen.simple.MDSourceBuilder;
import org.beetl.sql.gen.simple.MapperSourceBuilder;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * 内部测试新功能或者bug用，所有单元测试参考test目录
 * @author xiandafu
 *
 */

public class QuickTest {

	static DataSource dataSource = datasource();
	private static   DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");
		return ds;
	}
	private  static SQLManager getSQLManager(){

		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		builder.setProduct(false);
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}

	public static void main(String[] args) throws Exception {
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
		OrderLog para = new OrderLog();
		para.setOrderId(1);
		List list = sqlManager.select(SqlId.of("user","select"),OrderLog.class,para);
		System.out.println(list);
		sqlManager.select(SqlId.of("user","select"),OrderLog.class,para);

	}


	public static DataSource mysqlDatasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(MysqlDBConfig.url);
		ds.setUsername(MysqlDBConfig.userName);
		ds.setPassword(MysqlDBConfig.password);
		ds.setDriverClassName(MysqlDBConfig.driver);
		ds.setLeakDetectionThreshold(10);
		ds.setMaximumPoolSize(1);
		// ds.setAutoCommit(false);
		return ds;
	}

	public static class MysqlDBConfig {
		//    public static String driver = "com.mysql.jdbc.Driver";
		public static String driver = "com.mysql.cj.jdbc.Driver";
		public static String dbName = "test";
		public static String password = "123456";
		public static String userName = "root";
		public static String url = "jdbc:mysql://127.0.0.1:3306/" + dbName + "?&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true";
	}

}
