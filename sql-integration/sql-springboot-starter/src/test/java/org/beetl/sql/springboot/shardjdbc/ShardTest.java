package org.beetl.sql.springboot.shardjdbc;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;
import org.beetl.sql.springboot.masterslave.MasterSlaveApplication;
import org.beetl.sql.springboot.masterslave.MasterSlaveUserInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardApplication.class)
@Transactional
public class ShardTest {
	@Autowired
	SQLManager sqlManager;

	@Test
	public void test(){
		TOrder tOrder = 	sqlManager.unique(TOrder.class,1);
		System.out.println(tOrder);
	}
}
