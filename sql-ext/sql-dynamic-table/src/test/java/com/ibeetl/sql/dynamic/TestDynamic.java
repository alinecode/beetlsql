package com.ibeetl.sql.dynamic;

import com.beetl.sql.dynamic.BaseEntity;
import com.beetl.sql.dynamic.BeanTableAsmCode;
import com.beetl.sql.dynamic.DynamicEntityLoader;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
		//测试对其他类型生成要求，默认情况是生成Timestamp
		JavaType.mapping.put(java.sql.Types.TIMESTAMP,"java.time.LocalDateTime");

		DBInitHelper.executeSqlScript(sqlManager,"db/dynamic-schema.sql");
		DynamicEntityLoader<BaseEntity> dynamicEntityLoader = new DynamicEntityLoader(sqlManager);
		Class<? extends BaseEntity> c = dynamicEntityLoader.getDynamicEntity("order_log");
		BaseEntity baseEntity = sqlManager.unique(c,1);
		System.out.println(baseEntity.getValue("orderId"));
		System.out.println(baseEntity.getValue("age"));

		baseEntity.setValue("age",1);
		baseEntity.setValue("updateTime", LocalDateTime.now());
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


	@Test
	public void testCustomized() throws Exception{

		SQLManager sqlManager = getSQLManager();
		int max = 5;
		//创建5个表
		for(int i=0;i<max;i++){
			String dml="create table my_cc_table"+i+"(id int NOT NULL"
				+ ",name varchar(20)"
				+ ",PRIMARY KEY (`id`)"
				+ ") ";
			sqlManager.executeUpdate(new SQLReady(dml));
		}


		DynamicEntityLoader<Office> dynamicEntityLoader = new DynamicEntityLoader(sqlManager,"com.my",Office.class);

		for(int i=0;i<max;i++){
			Class<? extends Office> c = dynamicEntityLoader.getDynamicEntity("my_cc_table"+i);
			long  count = sqlManager.allCount(c);
			System.out.println(count);
		}


		for(int i=0;i<max;i++){
			Class<? extends Office> c = dynamicEntityLoader.getDynamicEntity("my_cc_table"+i);
			Office obj = c.newInstance();
			obj.setId(i);
			obj.setValue("name","hello");
			sqlManager.insert(obj);
		}

		for(int i=0;i<max;i++){
			Class<? extends Office> c = dynamicEntityLoader.getDynamicEntity("my_cc_table"+i);
			Office obj = c.newInstance();
			obj.setId(i);
			sqlManager.unique(obj.getClass(),i);
		}



	}

	/**
	 * 测试sql中的一些不常见类型，用于验证ASM字节码生成
	 * @throws Exception
	 */
	@Test
	public void testSqlType() throws Exception{

		SQLManager sqlManager = getSQLManager();
		String dml="create table my_type_table"+"(id int NOT NULL"
			+ ",name varchar(20)"
			+ ",content blob"
			+ ",create_time timestamp"
			+ ",PRIMARY KEY (`id`)"
			+ ") ";
		sqlManager.executeUpdate(new SQLReady(dml));


		DynamicEntityLoader<Office> dynamicEntityLoader = new DynamicEntityLoader(sqlManager,"com.my",Office.class);

		Class<? extends Office> c = dynamicEntityLoader.getDynamicEntity("my_type_table");
		Office obj = c.newInstance();
		obj.setId(1);
		obj.setValue("createTime",new Timestamp(System.currentTimeMillis()));
		sqlManager.insert(obj);

		long  count = sqlManager.allCount(c);
		Assert.assertEquals(1L,count);


	}




}
