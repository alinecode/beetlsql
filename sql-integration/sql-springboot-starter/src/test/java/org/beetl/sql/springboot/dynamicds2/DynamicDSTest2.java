package org.beetl.sql.springboot.dynamicds2;


import org.beetl.sql.springboot.dynamicds.Routing;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ThreadLocalRandom;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DynamicDSApplication2.class)
public class DynamicDSTest2 {

	@Autowired
	DynamicDSService2 dynamicDSService;

	@Autowired
    Routing routing;

	String db = "h2-xx1"+ ThreadLocalRandom.current().nextInt(100);
	String db2 = "mysql-yyx"+ThreadLocalRandom.current().nextInt(100);
	Integer id= 9;
    @Test
    public void testCommitH2(){
		routing.db(db);
		dynamicDSService.successCommit(db,id);
		Assert.assertTrue(dynamicDSService.testCommit(db,id));
    }

	@Test
	public void testCommitMysql(){
		routing.db(db2);
		dynamicDSService.successCommit(db2,id);
		Assert.assertTrue(dynamicDSService.testCommit(db2,id));
	}


	@Test
	public void testRollbackMysql(){
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
