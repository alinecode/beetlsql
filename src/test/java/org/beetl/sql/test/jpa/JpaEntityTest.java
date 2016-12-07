package org.beetl.sql.test.jpa;

import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.test.mysql.BaseMySqlTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-mysql-beetlsql.xml" })
@Transactional
public class JpaEntityTest extends BaseMySqlTest {
	@Before
	public void init() {
		super.init();
	}
	
	@Test
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
		
	}
}
