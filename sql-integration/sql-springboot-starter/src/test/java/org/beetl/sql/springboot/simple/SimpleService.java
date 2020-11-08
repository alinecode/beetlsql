package org.beetl.sql.springboot.simple;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
