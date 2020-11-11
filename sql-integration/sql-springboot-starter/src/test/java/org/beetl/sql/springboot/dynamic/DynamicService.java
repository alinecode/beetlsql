package org.beetl.sql.springboot.dynamic;

import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service

public class DynamicService {
	@Autowired
	@Qualifier("sqlManager")
	SQLManager sqlManager;

	@Autowired
	DynamicUserInfoMapper mapper;
	//混合多数据源，不支持事务,如果想具备事务功能，可以参考sega模块
	@Transactional(propagation = Propagation.NEVER)
	public void test(){
		mapper.deleteById(19999);
		sqlManager.single(UserInfoInDs1.class,1);
		sqlManager.single(UserInfoInDs2.class,1);
		mapper.single(1);
		UserInfoInDs2 userInfoInDs2 = mapper.queryById(1);
		System.out.println(userInfoInDs2.getId());
	}
}
