package org.beetlsql.sql.sega.test;

import org.beetl.sql.sega.common.SegaContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTest  extends BaseTest{
	@BeforeClass
	public static void init(){
		initTable(testSqlFile);
	}

	@Test
	public void simple(){
		SegaContext segaContext = SegaContext.segaContextFactory.current();
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
			segaContext.rollback();
		}
		long  afterCount = sqlManager.allCount(User.class);
		Assert.assertEquals(count,afterCount);
	}

	@Test
	public void stock(){
		SegaContext segaContext = SegaContext.segaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		String id ="1";
		Stock stock = sqlManager.unique(Stock.class,id);
		try{
			userMapper.addStock(id);
			userMapper.addStock(id);
			throw new RuntimeException("模拟异常");
		}catch(RuntimeException ex){
			segaContext.rollback();
		}
		Stock afterStock = sqlManager.unique(Stock.class,id);
		Assert.assertEquals(stock.getCount(),afterStock.getCount());
	}

}
