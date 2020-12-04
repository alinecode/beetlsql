package org.beetl.sql.springboot.simple;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class SimpleService {
	@Autowired
	SQLManager sqlManager;

	@Autowired
	SimpleUserInfoMapper userInfoMapper;

	@Transactional
	public void test(){
		sqlManager.single(UserInfo.class,1);
		userInfoMapper.single(1);
		userInfoMapper.select();
	}

	@Transactional(readOnly = true,timeout = 1)
	public void timeout()  {
		sqlManager.single(UserInfo.class,1);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			//不可能发生
			throw new RuntimeException(e);
		}
		try{
			sqlManager.single(UserInfo.class,1);
			userInfoMapper.single(1);
		}catch(RuntimeException re){
			throw new IllegalStateException("timeout");
		}


	}
}
