package org.beetl.sql.springboot.simple;


import org.beetl.sql.springboot.UserInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleApplication.class)
public class SimpleTest {
	@Autowired
	SimpleService service;

    @Test
    public void test(){
		service.test();
    }

	@Test
	public void exception(){
    	UserInfo info = service.queryUser(1);
    	String name = info.getName();
    	try{
			service.exception();
			Assert.fail();
		}catch (Exception exception){

		}
		UserInfo info2 = service.queryUser(1);
    	Assert.assertEquals(name,info2.getName());

	}

	@Test
	public void timeout(){
    	try{
			service.timeout();
			Assert.fail();
		}catch(IllegalStateException sqlException){
			Assert.assertEquals("timeout",sqlException.getMessage());
		}



	}
}