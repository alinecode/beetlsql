package org.beetl.sql.springboot.datasource2.db1;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class Db1Service {
	@Autowired
	@Qualifier("sqlManager1")
	SQLManager sqlManager;

	@Autowired
	Db1Mapper userInfoMapper;



	@Transactional
	public UserInfo queryUser(Integer id){
		return sqlManager.single(UserInfo.class,id);

	}


}
