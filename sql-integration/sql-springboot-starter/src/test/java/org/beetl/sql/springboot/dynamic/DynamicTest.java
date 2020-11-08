package org.beetl.sql.springboot.dynamic;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.springboot.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DynamicApplication.class)
public class DynamicTest {

	@Autowired
	DynamicService dynamicService;

    @Test
    public void test(){
		dynamicService.test();
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