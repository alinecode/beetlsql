package org.beetl.sql.springboot.simple;


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
	public void timeout(){
    	try{
			service.timeout();
			Assert.fail();
		}catch(IllegalStateException sqlException){
			Assert.assertEquals("timeout",sqlException.getMessage());
		}



	}
}