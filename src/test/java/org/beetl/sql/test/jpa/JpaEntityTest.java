package org.beetl.sql.test.jpa;

import java.util.List;

import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.test.mysql.BaseMySqlTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.junit.Assert;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:applicationContext-mysql-beetlsql.xml" })
//@Transactional
public class JpaEntityTest extends BaseMySqlTest {
	@Before
	public void init() {
		super.init();
	}
	
//	@Test
	public void testSave(){
		TestEntity testEntity=new TestEntity();
		testEntity.setId("1234567890");
		testEntity.setAge(20);
		testEntity.setBigger("测试用例Blob类型".getBytes());
		testEntity.setBiggerClob("测试用例clob类型");
		testEntity.setLoginName("admin");
		testEntity.setPassword("123qwe");
		testEntity.setTtSize(System.currentTimeMillis());
		/*
		KeyHolder k=new KeyHolder();
		k.setKey("1234567890");
		int r=sqlManager.insert(testEntity.getClass(), testEntity,k );
		*/
		int r=sqlManager.insert(testEntity);
		Assert.assertTrue(r==1);
		
		testEntity.setPassword("qweasd");
		r=sqlManager.updateById(testEntity);
		Assert.assertTrue(r==1);
		
		List<TestEntity> testList=sqlManager.all(TestEntity.class);
		Assert.assertTrue(testList.size()>0);
		
		TestEntity uniqueTestEntity=sqlManager.unique(TestEntity.class, testEntity.getId());
		Assert.assertNotNull(uniqueTestEntity);
		
		
		List<TestEntity> executeList= sqlManager.execute(new SQLReady("select id,login_name,age from PF_TEST where login_name like concat('%',concat(?,'%'))","mi"), TestEntity.class);
		Assert.assertTrue(executeList.size()>0);
		
		
	}
	
//	@Test
	public void testUpdateTemplateById(){
		TestEntity testEntity=new TestEntity();
		testEntity.setId("1234567890");
		testEntity.setAge(20);
		testEntity.setBigger("测试用例Blob类型".getBytes());
		testEntity.setBiggerClob("测试用例clob类型");
		testEntity.setLoginName("admin");
		testEntity.setPassword("123qwe");
		testEntity.setTtSize(System.currentTimeMillis());
		
		int r=sqlManager.insert(testEntity);
		Assert.assertTrue(r==1);
		
		TestEntity updateTestEntity=new TestEntity();
		updateTestEntity.setId(testEntity.getId());
		updateTestEntity.setLoginName("beetlSqlAdmin");
		r=sqlManager.updateTemplateById(updateTestEntity);
		Assert.assertTrue(r==1);
		
		TestEntity updateTestEntityForCheck=sqlManager.unique(TestEntity.class, testEntity.getId());
		Assert.assertNotNull(updateTestEntityForCheck);
	}
	
	
}
