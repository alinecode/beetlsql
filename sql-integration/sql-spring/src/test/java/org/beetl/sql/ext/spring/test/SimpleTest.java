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
@ContextConfiguration(locations = {"classpath*:spring-simple.xml"})
public class SimpleTest {

	@Autowired
	SQLManager sqlManager;
    @Autowired
    UserService userService;

    @Before
	public void init(){
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
	}

    @Test
    public void test(){
        UserInfo info = new UserInfo();
        info.setId(199999l);
        info.setName("hello");
        userService.addUser(info);

		UserInfo one = userService.selectOne();



    }



    @Test
    public void testTransaction(){
        UserInfo info = userService.getById(1l);
        Assert.assertNotNull(info);
        try{
            userService.delete(info.getId());
        }catch(RuntimeException ex){

        }

        info = userService.getById(1l);
        Assert.assertNotNull(info);
        System.out.println();


    }

}
