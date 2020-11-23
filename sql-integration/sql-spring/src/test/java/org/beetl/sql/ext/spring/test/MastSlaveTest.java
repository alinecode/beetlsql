package org.beetl.sql.ext.spring.test;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations = {"classpath*:spring-master-salve-datasource.xml"})
public class MastSlaveTest {
    @Autowired
    UserService userService;
	@Autowired
	SQLManager sqlManager;
	@Before
	public void init(){
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
	}
    @Test
    public void test(){
        UserInfo info = userService.getById(1l);
        Assert.assertNotNull(info);
    }



}
