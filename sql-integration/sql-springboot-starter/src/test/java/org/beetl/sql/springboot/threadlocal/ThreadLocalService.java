package org.beetl.sql.springboot.threadlocal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.ThreadLocalSQLManager;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service

public class ThreadLocalService {
	@Autowired
	@Qualifier("proxySqlManager")
	SQLManager sqlManager;

	@Autowired
    UserInfoMapper mapper;
	//混合多数据源，spring 不支持全局事务,如果想具备事务功能，可以参考saga模块
	@Transactional(propagation = Propagation.NEVER)
	public void test(){
		use("sqlManager1");

		UserInfo info1 = sqlManager.single(UserInfo.class,1);
		info1.setName(info1.getName()+" 1999");
		sqlManager.updateById(info1);
		info1 =  sqlManager.single(UserInfo.class,1);
		use("sqlManager2");
		UserInfo info2 = sqlManager.single(UserInfo.class,1);
		Assert.assertNotEquals(info1.getName(),info2.getName());
		info2.setName(info2.getName()+" 1999");
		mapper.updateById(info2);
		info2 = mapper.single(info2.getId());
		Assert.assertEquals(info1.getName(),info2.getName());


	}

	protected  void use(String sqlManager){
		ThreadLocalSQLManager.locals.set(sqlManager);
	}
}
