package org.beetl.sql.springboot.threadlocal;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThreadLocalApplication.class)
public class ThreadLocalTest {

	@Autowired
	ThreadLocalService dynamicService;

    @Test
    public void test(){
		dynamicService.test();
    }


	@Test
	public void testWithAnnotation(){
		long dbCount1 = dynamicService.test1();
		long dbCount2 = dynamicService.test2();
		Assert.assertNotEquals(dbCount1,dbCount2);
	}


    /*以下代码初始化数据库用*/
	@Autowired
	@Qualifier("sqlManager1")
	SQLManager sqlManager1;

	@Autowired
	@Qualifier("sqlManager2")
	SQLManager sqlManager2;


	@Before
	public void init(){
		DBInitHelper.executeSqlScript(sqlManager1,"db/schema.sql");
		DBInitHelper.executeSqlScript(sqlManager2,"db/schema2.sql");
	}
}