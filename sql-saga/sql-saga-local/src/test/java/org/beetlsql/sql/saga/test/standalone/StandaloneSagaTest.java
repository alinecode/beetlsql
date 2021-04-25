package org.beetlsql.sql.saga.test.standalone;

import org.beetl.sql.saga.common.LocalSagaContext;
import org.beetl.sql.saga.common.LocalSagaContextFactory;
import org.beetl.sql.saga.common.SagaContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 单独运行或者与其他框架集成的示例
 */
public class StandaloneSagaTest extends BaseTest{
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
			//嵌套
			SagaContext sagaContext2= SagaContext.sagaContextFactory.current();
			try{
				sagaContext2.start();
				User user2 = new User();
				user2.setName("abc");
				userMapper.insert(user2);
				sagaContext2.commit();
			}catch(RuntimeException re){
				//没有回滚
				sagaContext2.rollback();
				throw re;
			}
			if(1==1){
				throw new RuntimeException("模拟异常");
			}
			sagaContext.commit();
		}catch(RuntimeException ex){
			//开始回滚
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
				//必须再抛出
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
