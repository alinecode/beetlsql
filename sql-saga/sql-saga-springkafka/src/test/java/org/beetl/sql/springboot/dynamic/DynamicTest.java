package org.beetl.sql.springboot.dynamic;


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
@SpringBootTest(classes = DynamicApplication.class)
public class DynamicTest {

	@Autowired
	DynamicService dynamicService;

    @Test
    public void nomal(){
    	dynamicService.normal();
    	//回滚成功
		UserInfoInDs1 ds1 = sqlManager1.single(UserInfoInDs1.class,100);
		UserInfoInDs2 ds2 = sqlManager2.single(UserInfoInDs2.class,100);
		Assert.assertNull(ds1);
		Assert.assertNull(ds2);
    }

	@Test
	public void dbDown() throws InterruptedException {
		dynamicService.dbDown();
	}

	@Test
	public void dbDownAndStart() throws InterruptedException {
		dynamicService.dbDown();

		//恢复数据库
		dynamicService.recreateTable();
		//等待回滚成功
		Thread.sleep(1000*10);
		//回滚成功
		UserInfoInDs1 ds1 = sqlManager1.single(UserInfoInDs1.class,100);
		Assert.assertNull(ds1);

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
		DBInitHelper.executeSqlScript(sqlManager2,"db/schema.sql");
	}
}