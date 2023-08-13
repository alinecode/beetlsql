package com.ibeetl.sql.dynamic;

import com.beetl.sql.dynamic.BaseEntity;
import com.beetl.sql.dynamic.DynamicEntityLoader;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

public class TestDynamic {
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
	public void testDynamic(){
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/dynamic-schema.sql");
		DynamicEntityLoader<BaseEntity> dynamicEntityLoader = new DynamicEntityLoader(sqlManager);
		Class<? extends BaseEntity> c = dynamicEntityLoader.getDynamicEntity("order_log");
		BaseEntity baseEntity = sqlManager.unique(c,1);
		System.out.println(baseEntity.getValue("orderId"));
		System.out.println(baseEntity.getValue("age"));

		baseEntity.setValue("age",1);
		sqlManager.updateById(baseEntity);

		Class<? extends BaseEntity> c2 = dynamicEntityLoader.getDynamicEntity("order_log",BaseEntity.class);
		List<BaseEntity> list = (List<BaseEntity>) sqlManager.all(c2);
		System.out.println(list.size());

	}

	@Test
	public void testDynamic2() throws Exception{

		SQLManager sqlManager = getSQLManager();
		int max = 5;
		//创建5个表
		for(int i=0;i<max;i++){
			String dml="create table my_table"+i+"(id int NOT NULL"
				+ ",name varchar(20)"
				+ ",PRIMARY KEY (`id`)"
				+ ") ";
			sqlManager.executeUpdate(new SQLReady(dml));
		}


		DynamicEntityLoader<BaseEntity> dynamicEntityLoader = new DynamicEntityLoader(sqlManager);

		for(int i=0;i<max;i++){
			Class<? extends BaseEntity> c = dynamicEntityLoader.getDynamicEntity("my_table"+i);
			long  count = sqlManager.allCount(c);
			System.out.println(count);
		}


		for(int i=0;i<max;i++){
			Class<? extends BaseEntity> c = dynamicEntityLoader.getDynamicEntity("my_table"+i);
			BaseEntity obj = c.newInstance();
			obj.setValue("id",1);
			obj.setValue("name","hello");
			sqlManager.insert(obj);
		}



	}


	/**
	 * 测试使用任何对象构造dynamicLoader
	 */
	@Test
	public void testCustomized(){
		SQLManager sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager,"db/dynamic-schema.sql");
		DynamicEntityLoader<Office> dynamicEntityLoader = new DynamicEntityLoader(sqlManager,"com.my",Office.class);

		Class<? extends Office> c = dynamicEntityLoader.getDynamicEntity("order_log");
		Office office = sqlManager.unique(c,1);
		System.out.println(office.getValue("orderId"));
		System.out.println(office.getValue("age"));

		office.setValue("age",1);
		sqlManager.updateById(office);


	}




}
