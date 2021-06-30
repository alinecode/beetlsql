package org.beetl.sql.test;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

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
		builder.setDbStyle(new MySqlStyle());
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}

    public static void main(String[] args) throws Exception {
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");

		LambdaQuery<Order> lambdaQuery = sqlManager.lambdaQuery(Order.class);
		PageResult<Order> pageQuery = lambdaQuery.andEq(Order::getUserId,1).andEq(Order::getUserName,"ab").orderBy("id").page(1,10);

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
