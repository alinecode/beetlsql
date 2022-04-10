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
	ThreadLocalService self;

	@Autowired
    UserInfoMapper mapper;
	@Transactional(propagation = Propagation.REQUIRED)
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


		use("sqlManager1");
		long count = mapper.allCount();
		use("sqlManager2");
		long count2 = mapper.allCount();
		Assert.assertTrue(count!=count2);
		System.out.println(count+" "+count2);

	}



	@Use("sqlManager2")
	public long test2(){
		return mapper.allCount();

	}


	@Use("sqlManager1")
	public long test1(){
		return mapper.allCount();
	}


	/**
	 * 如果没有使用use，则使用默认
	 * @return
	 */
	public long testDefault(){
		return mapper.allCount();
	}

	@Use("sqlManager1")
	public void testNested(){
		long count1 = mapper.allCount();
		long count2 = self.test2();
		//回到"sqlManager1"
		long count1_1 = mapper.allCount();
		//使用默认
		long defaultCont = self.testDefault();
	}




	protected  void use(String sqlManager){
		ThreadLocalSQLManager.locals.set(sqlManager);
	}
}
