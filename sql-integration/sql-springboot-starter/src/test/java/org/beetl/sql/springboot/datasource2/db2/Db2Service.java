package org.beetl.sql.springboot.datasource2.db2;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Db2Service {
	@Autowired
	@Qualifier("sqlManager2")
	SQLManager sqlManager;

	@Autowired
	Db2Mapper userInfoMapper;


	@Transactional
	public UserInfo queryUser(Integer id){
		return sqlManager.single(UserInfo.class,id);

	}



}
