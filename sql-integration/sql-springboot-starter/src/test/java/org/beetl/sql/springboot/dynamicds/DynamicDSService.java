package org.beetl.sql.springboot.dynamicds;

import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DynamicDSService {


	@Autowired
	SQLManager sqlManager; //默认

	@Autowired
	DeptMapper deptMapper;

	@Autowired
	Routing routing;
	protected void insertDept(int deptId){

		Dept department = new Dept();
		department.setId(deptId);
		department.setName("hello ");
		//可以用sqlmanager或者 mapper操作
//		sqlManager.insert(department);
		deptMapper.insert(department);
	}

	/**
	 *
	 * @param dsName
	 * @param deptId
	 */
	@Transactional(rollbackFor =RuntimeException.class,transactionManager="defaultTs" )
	public void successCommit(String dsName,int deptId){
		insertDept(deptId);

	}


	/**
	 * 手工回滚事务
	 * @param dsName
	 * @param deptId
	 */
	@Transactional(rollbackFor =RuntimeException.class,transactionManager="defaultTs" )
	public void rollback(String dsName,int deptId){
		insertDept(deptId);
		throw new RuntimeException("模拟回滚");


	}

	/**
	 * 测试数据是否存在
	 * @param dsName
	 * @param depId
	 * @return
	 */
	public boolean testCommit(String dsName,int depId){
		return sqlManager.exist(Dept.class,depId);
	}


}
