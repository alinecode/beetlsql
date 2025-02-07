package org.beetl.sql.springboot.dynamicds;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.springboot.dynamic.DynamicApplication;
import org.beetl.sql.springboot.dynamic.DynamicService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ThreadLocalRandom;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DynamicDSApplication.class)
public class DynamicDSTest {

	@Autowired
    DynamicDSService dynamicDSService;

	@Autowired
	Routing routing;

	String db = "hello-go"+ ThreadLocalRandom.current().nextInt(100);
	String db2 = "hi-marry"+ThreadLocalRandom.current().nextInt(100);
	Integer id= 9;
    @Test
    public void testCommit(){
		routing.db(db);
		dynamicDSService.successCommit(db,id);
		Assert.assertTrue(dynamicDSService.testCommit(db,id));
    }

	@Test
	public void testRollback(){
		try{
			routing.db(db2);
			dynamicDSService.rollback(db2,id);
			Assert.fail();
		}catch (RuntimeException exception){
			//异常
		}

		Assert.assertFalse(dynamicDSService.testCommit(db2,id));
	}



}
