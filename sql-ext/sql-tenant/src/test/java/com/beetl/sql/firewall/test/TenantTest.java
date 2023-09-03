package com.beetl.sql.firewall.test;

import com.beetl.sql.tenant.ColRewriteParam;
import com.beetl.sql.tenant.ColValueProvider;
import com.beetl.sql.tenant.TenantConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TenantTest {
	static DataSource dataSource = datasource();

	static SQLManager sqlManager;

	static  MyRewriteMapper myTenantMapper;

	@BeforeClass
	public  static void init(){
		sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");

		//配置多租户功能支持，
		TenantConfig tenantConfig = new TenantConfig();
		tenantConfig.config(sqlManager);
		tenantConfig.addColRewriteConfig(new ColRewriteParam("tenant_id", new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return localValue.get();
			}
		}));


		myTenantMapper = sqlManager.getMapper(MyRewriteMapper.class);
	}

	//模拟大多数租户使用ThreadLocal
	static ThreadLocal<Integer> localValue = ThreadLocal.withInitial(new Supplier<Integer>() {
		@Override
		public Integer get() {
			return null;
		}
	});

	@Test
	public void test(){

		//租户1
		localValue.set(1);
		List<OrderLog> logs = myTenantMapper.all();
		Assert.assertEquals(2,logs.size());
		//租户2
		localValue.set(2);
		logs = myTenantMapper.all();
		Assert.assertEquals(2,logs.size());

		//直接使用sqlmanager，忽略租户
		logs =  sqlManager.all(OrderLog.class);
		Assert.assertEquals(4,logs.size());

		localValue.set(2);
		List<OrderLog> list = myTenantMapper.select("b");
		Assert.assertEquals(2,list.size());

		MyCommonMapper myCommonMapper = sqlManager.getMapper(MyCommonMapper.class);
		//忽略租户
		list = myCommonMapper.select("b");
		Assert.assertEquals(3,list.size());


		//测试禁止sql重写
		list = myTenantMapper.select2("b");
		Assert.assertEquals(3,list.size());


		OrderLog orderLog = new OrderLog();
		orderLog.setId(99);
		orderLog.setName("any");
		orderLog.setTenantId(5);
		myTenantMapper.insert(orderLog);




	}


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


}
