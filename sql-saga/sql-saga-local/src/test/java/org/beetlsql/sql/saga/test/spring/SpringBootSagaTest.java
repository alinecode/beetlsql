package org.beetlsql.sql.saga.test.spring;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.saga.common.LocalSagaContext;
import org.beetl.sql.saga.common.SagaContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * springboot集成测试，测试内容同standalone.SimpleTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringSagaLocalApplication.class)
public class SpringBootSagaTest {

	@Autowired
	SQLManager sqlManager;


	@Before
	public void init(){
		DBInitHelper.executeSqlScript(sqlManager,"db/db-init.sql");
	}

    @Test
    public void test(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		long count = sqlManager.allCount(User.class);
		try{
			sagaContext.start();
			User user = new User();
			user.setName("abc");
			userMapper.insert(user);
			User user2 = new User();
			user2.setName("abc");
			userMapper.insert(user2);
			if(1==1){
				throw new RuntimeException("模拟异常");
			}
			sagaContext.commit();
		}catch(RuntimeException ex){
			sagaContext.rollback();
		}
		long  afterCount = sqlManager.allCount(User.class);
		Assert.assertEquals(count,afterCount);
    }


	@Test
	public void stock(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		String id ="1";
		Stock stock = sqlManager.unique(Stock.class,id);
		try{
			sagaContext.start();
			userMapper.addStock(id);
			userMapper.addStock(id);
			if(true)throw new RuntimeException("模拟异常");
			sagaContext.commit();
		}catch(RuntimeException ex){
			sagaContext.rollback();
			return ;
		}
		Stock afterStock = sqlManager.unique(Stock.class,id);
		Assert.assertEquals(stock.getCount(),afterStock.getCount());
	}


	@Test
	public void nested(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		long count = sqlManager.allCount(User.class);
		try{
			sagaContext.start();
			User user = new User();
			user.setName("abc");
			userMapper.insert(user);
			//嵌套，一般是在另外一个方法里
			SagaContext sagaContext2= SagaContext.sagaContextFactory.current();
			try{
				sagaContext2.start();
				User user2 = new User();
				user2.setName("abc");
				userMapper.insert(user2);
				sagaContext2.commit();
			}catch(RuntimeException re){
				sagaContext2.rollback();
				throw re;
			}
			if(1==1){
				throw new RuntimeException("模拟异常");
			}
			sagaContext.commit();
		}catch(RuntimeException ex){
			//开始回滚
			ex.printStackTrace();
			sagaContext.rollback();
		}
		long  afterCount = sqlManager.allCount(User.class);
		Assert.assertEquals(count,afterCount);
	}

	@Test
	public void nested2(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		long count = sqlManager.allCount(User.class);
		try{
			sagaContext.start();
			User user = new User();
			user.setName("abc");
			userMapper.insert(user);
			//嵌套
			SagaContext sagaContext2= SagaContext.sagaContextFactory.current();
			try{
				sagaContext2.start();
				User user2 = new User();
				user2.setName("abc");
				userMapper.insert(user2);
				if(1==1){
					throw new RuntimeException("模拟异常");
				}
				sagaContext2.commit();
			}catch(RuntimeException re){
				sagaContext2.rollback();
				//同其他事物框架一样，必须再抛出
				throw re;
			}
			sagaContext.commit();
		}
		catch(RuntimeException ex){
			//开始回滚
			sagaContext.rollback();
		}
		long  afterCount = sqlManager.allCount(User.class);
		Assert.assertEquals(count,afterCount);
	}


}