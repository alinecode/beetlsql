package org.beetl.sql.springboot.dynamicds;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring.SpringConnectionSource;
import org.beetl.sql.springboot.simple.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

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
