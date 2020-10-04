package org.beetl.sql.ext.spring.test.dynamic;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.spring.test.UserInfo;
import org.beetl.sql.ext.spring.test.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations = {"classpath*:spring-dynamic-sqlmanager.xml"})
public class SpringDynamicTest {
    @Autowired
    @Qualifier("dynamicSqlManagerFactoryBean")
    SQLManager sqlManager;


	/*sqlManager1和sqlManager2 用于初始化*/
	@Autowired
	@Qualifier("sqlManagerFactoryBean1")
	SQLManager sqlManager1;

	@Autowired
	@Qualifier("sqlManagerFactoryBean2")
	SQLManager sqlManager2;

    @Autowired
    DbUser1Mapper dbUser1Mapper;

    @Autowired
    DbUser2Mapper dbUser2Mapper;


	@Before
	public void init(){
		DBInitHelper.executeSqlScript(sqlManager1,"db/schema.sql");
		DBInitHelper.executeSqlScript(sqlManager2,"db/schema.sql");
	}

    @Test
    public void test(){
        DbUser1 db1User = sqlManager.unique(DbUser1.class,1);
        DbUser2 db2User = sqlManager.unique(DbUser2.class,1);
        Assert.assertEquals(db1User.getId(),db2User.getId());
    }


    @Test
    public void testByMapper(){
        DbUser1 db1User = dbUser1Mapper.unique(1);
        DbUser2 db2User = dbUser2Mapper.unique(1);
        Assert.assertEquals(db1User.getId(),db2User.getId());
    }


}
