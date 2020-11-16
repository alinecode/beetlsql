package org.beetl.sql.springboot.dynamic;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service

public class DynamicService {


	@Autowired
	UserInfoDs1Mapper userInfoDs1Mapper;


	@Autowired
	UserInfoDs2Mapper userInfoDs2Mapper;


	/**
	 * 模拟失败时候回滚
	 * @return
	 */
	@Transactional(propagation = Propagation.NEVER)
	public boolean normal(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		try{
			UserInfoInDs1 ds1 = new UserInfoInDs1();
			ds1.setId(100);
			ds1.setName("ces");

			UserInfoInDs2 ds2 = new UserInfoInDs2();
			ds2.setId(100);
			ds2.setName("abs");
			//俩个数据库
			userInfoDs1Mapper.insert(ds1);
			userInfoDs2Mapper.insert(ds2);
			//模拟一个错误
			int a = 1/0;
		}catch(Exception ex){
			sagaContext.rollback();
			return false;
		}
		return true;
	}

	/**
	 * 模拟数据库挡掉后，回滚失败，放入失败队列
	 * @return
	 */
	@Transactional(propagation = Propagation.NEVER)
	public boolean dbDown(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		try{
			UserInfoInDs1 ds1 = new UserInfoInDs1();
			ds1.setId(100);
			ds1.setName("ces");

			UserInfoInDs2 ds2 = new UserInfoInDs2();
			ds2.setId(100);
			ds2.setName("abs");
			//俩个数据库
			userInfoDs1Mapper.insert(ds1);
			//删除表ds1和ds2，导致ds2插入失败，且回滚失败
			rmTable();
			userInfoDs2Mapper.insert(ds2);

		}catch(Exception ex){
			sagaContext.rollback();
			return false;
		}
		return true;
	}






	@Autowired
	@Qualifier("sqlManager1")
	SQLManager sqlManager1;

	@Autowired
	@Qualifier("sqlManager2")
	SQLManager sqlManager2;



	protected  void rmTable(){
		sqlManager1.executeUpdate(new SQLReady("drop table sys_user"));
		sqlManager2.executeUpdate(new SQLReady("drop table sys_user"));
	}

	public  void recreateTable(){
		DBInitHelper.executeSqlScript(sqlManager1,"db/schema.sql");
		DBInitHelper.executeSqlScript(sqlManager2,"db/schema.sql");
	}
}
