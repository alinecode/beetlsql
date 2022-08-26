package org.beetl.sql.springboot.simple;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SimpleService {
	@Autowired
	SQLManager sqlManager;

	@Autowired
	SimpleUserInfoMapper userInfoMapper;

	@Autowired
	ApplicationContext applicationContext;

	@Transactional
	public void loadSql(){
		userInfoMapper.select();
	}

	@Transactional
	public void test(){
		sqlManager.single(UserInfo.class,1);
		userInfoMapper.single(1);
		userInfoMapper.select();

		userInfoMapper.createLambdaQuery().select();
		userInfoMapper.createLambdaQuery().select();
	}


	@Transactional
	public void reflect(){
		Object userInfoMapper = applicationContext.getBean(SimpleUserInfoMapper.class);
		Class cls = userInfoMapper.getClass();
		System.out.println(Arrays.asList(cls.getMethods()));
		try {
			Method method  = cls.getMethod("insert",new Class[]{Object.class});
			System.out.println(method);
//			method.invoke(userInfoMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public UserInfo queryUser(Integer id){
		return sqlManager.single(UserInfo.class,id);

	}


	@Transactional
	public void exception(){
		UserInfo info = sqlManager.single(UserInfo.class,1);
		info.setName("abc+exception");
		userInfoMapper.updateById(info);
		int a = 1/0;
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


	/**
	 * 一个虚拟表的测试，用于分表
	 */
	@Transactional
	public void toTable(){
		Department dept = sqlManager.single(Department.class,1);
		String sql =  "select * from ${toTable('department')} where id = 1";
		List<Department> list1 = sqlManager.execute(sql,Department.class,null);
		//使用真实的一个表
		List<Department> list2 = sqlManager.lambdaQuery(Department.class).asTable("department")
				.andEq(Department::getId,1).select();
		//高速Query类，这个是是个虚拟表
		List<Department> list3 = sqlManager.lambdaQuery(Department.class).virtualTable()
				.andEq(Department::getId,1).select();

		Department dataValue = new Department();
		dataValue.setId(10);
		dataValue.setName("hello");
		sqlManager.insert(dataValue);

		dataValue.setId(11);
		DepartmentMapper departmentMapper = sqlManager.getMapper(DepartmentMapper.class);
		departmentMapper.insert(dataValue);


	}
}
