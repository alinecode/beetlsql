package org.beetl.sql.test;


import com.beetl.sql.pref.PerformanceConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.ClassDesc;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.*;
import org.beetl.sql.core.call.CallReady;
import org.beetl.sql.core.call.InArg;
import org.beetl.sql.core.call.OutArg;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 内部测试新功能或者bug用，所有单元测试参考test目录
 * @author xiandafu
 *
 */

public class QuickTest {

	static DataSource dataSource = mysqlDatasource();
	private static   DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");
		ds.setSchema(null);
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
		BeanKit.JAVABEAN_STRICT = false;
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
		for(int i=0;i<10;i++){
			long start = System.currentTimeMillis();
			System.out.println();
			TableDesc tableDesc = sqlManager.getTableDesc("order_log");
			long end = System.currentTimeMillis();
			System.out.println(i+"="+(end-start));

		}


//		OrderLog orderLog = new OrderLog();
//		orderLog.setOrderId(1);
//		orderLog.setName("a");
//		sqlManager.upsertByTemplate(orderLog);
//
//		orderLog = sqlManager.unique(OrderLog.class,1);
//		System.out.println(orderLog);

		CallReady callReady = new CallReady("call test.logcount(?,?)");
		callReady.add(1,new InArg(1));
		callReady.add(2,new OutArg(Integer.class));
		sqlManager.executeCall(callReady);

		Object ret = callReady.getOutValue(2);
		System.out.println(ret);


		OrderLogMapper logMapper = sqlManager.getMapper(OrderLogMapper.class);
		OutHolder outHolder = new OutHolder();
		logMapper.logcount(1,outHolder);
		System.out.println(outHolder.getId());



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
		public static String password = "12345678";
		public static String userName = "root";
		public static String url = "jdbc:mysql://127.0.0.1:3306/" + dbName + "?&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true";
	}

}
