package org.beetl.sql.test;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.kit.AutoSQLEnum;
import org.beetl.sql.core.*;
import org.beetl.sql.core.call.CallReady;
import org.beetl.sql.core.call.InArg;
import org.beetl.sql.core.call.OutArg;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
		PageRequest pageRequest = DefaultPageRequest.of(1,10);
		sqlManager.all(OrderLog.class);
//		PageResult<QuickSubUser> users = sqlManager.pageQuery(SqlId.of("user.select"),QuickSubUser.class,null,pageRequest);
//		System.out.println(users);
//		users = sqlManager.pageQuery(SqlId.of("user.select"),QuickSubUser.class,null,pageRequest);
//		System.out.println(users);

	}

	private static Date getDate(String str){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sd.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
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
