package org.beetlsql.sql.saga.test;

import org.beetl.sql.saga.common.LocalSagaContext;
import org.beetl.sql.saga.common.LocalSagaContextFactory;
import org.beetl.sql.saga.common.SagaContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTest  extends BaseTest{
	@BeforeClass
	public static void init(){
		initTable(testSqlFile);
		//使用本地
		SagaContext.sagaContextFactory = new LocalSagaContextFactory();
	}

	@Test
	public void simple(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		long count = sqlManager.allCount(User.class);
		try{
			User user = new User();
			user.setName("abc");
			userMapper.insert(user);
			User user2 = new User();
			user2.setName("abc");
			userMapper.insert(user2);
			throw new RuntimeException("模拟异常");
		}catch(RuntimeException ex){
			sagaContext.rollback();
		}
		long  afterCount = sqlManager.allCount(User.class);
		Assert.assertEquals(count,afterCount);
	}

	@Test
	public boolean stock(){
		SagaContext sagaContext = LocalSagaContext.sagaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		String id ="1";
		Stock stock = sqlManager.unique(Stock.class,id);
		try{
			userMapper.addStock(id);
			userMapper.addStock(id);
			if(true)throw new RuntimeException("模拟异常");
		}catch(RuntimeException ex){
			sagaContext.rollback();
			//操作失败，如果是微服务，需要告诉调用方，失败了，以便让调用发回滚自己的事务
			return false;
		}
		Stock afterStock = sqlManager.unique(Stock.class,id);
		Assert.assertEquals(stock.getCount(),afterStock.getCount());
		return true;
	}

}
