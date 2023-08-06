package com.beetl.sql.firewall;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.firewall.FireWall;
import org.beetl.sql.firewall.FireWallConfig;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class TestFirewall {
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

	@Test
	public void test(){
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");

		FireWall fireWall = new FireWall().setDmlCreateEnable(false).setSqlMaxLength(50);
		FireWallConfig fireWallConfig = new FireWallConfig(fireWall);
		fireWallConfig.config(sqlManager);

		try{
			String sql = "delete from order_log";
			sqlManager.executeUpdate(new SQLReady(sql));
			Assert.fail();
		}catch (Exception exception){
			Assert.assertTrue(exception instanceof  BeetlSQLException);
		}

		try{
			String sql = "update table order_log set age=1";
			sqlManager.executeUpdate(new SQLReady(sql));
			Assert.fail();
		}catch (Exception exception){
			Assert.assertTrue(exception instanceof  BeetlSQLException);
		}


		try{
			String sql = "create table Foo (id int)";
			sqlManager.executeUpdate(new SQLReady(sql));
			Assert.fail();
		}catch (Exception exception){
			Assert.assertTrue(exception instanceof  BeetlSQLException);
		}




		try{
			//测试超长sql
			String sql = "select * from order_log where id = 1 and d = 1 and d = 1 and d = 1 and d = 1 and d = 1 and d = 1 and d = 1 and d = 1 and d = 1 and d = 1 and d = 1 and ";
			sqlManager.executeUpdate(new SQLReady(sql));
			Assert.fail();
		}catch (Exception exception){
			Assert.assertTrue(exception instanceof  BeetlSQLException);
		}






	}



}
