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

public class DynamicDSService {

	ConcurrentHashMap<String,DataSource> dsMap = new ConcurrentHashMap<>();
	ConcurrentHashMap<String, SQLManager> sqlManagerConcurrentHashMap = new ConcurrentHashMap<>();


	@Autowired
	SQLManager sqlManager; //默认

	@Autowired
	ApplicationContext context;
	protected void insertDept(String dsName,int deptId){
		//dao

		SQLManager sqlManager = sqlManager(dsName);
		Dept department = new Dept();
		department.setId(deptId);
		department.setName("hello");
		sqlManager.insert(department);
	}

	/**
	 * 手工提交事物
	 * @param dsName
	 * @param deptId
	 */
	public void successCommit(String dsName,int deptId){
		DataSourceTransactionManager dataSourceTransactionManager = this.getTS(dsName);
		TransactionStatus transactionStatus = create(dsName);
		insertDept(dsName,deptId);
		dataSourceTransactionManager.commit(transactionStatus);
	}


	/**
	 * 手工回滚事务
	 * @param dsName
	 * @param deptId
	 */
	public void rollback(String dsName,int deptId){
		DataSourceTransactionManager dataSourceTransactionManager = this.getTS(dsName);
		TransactionStatus transactionStatus = create(dsName);
		try{
			insertDept(dsName,deptId);
			throw new RuntimeException("模拟回滚");
		}catch (Exception  ex){
			dataSourceTransactionManager.rollback(transactionStatus);
		}


	}

	/**
	 * 测试数据是否存在
	 * @param dsName
	 * @param depId
	 * @return
	 */
	public boolean testCommit(String dsName,int depId){
		SQLManager sqlManager = sqlManager(dsName);
		return sqlManager.exist(Dept.class,depId);
	}

	/**
	 * 容器管理事务
	 * @param deptId
	 */
	@Transactional(rollbackFor = RuntimeException.class,transactionManager="defaultTs")
	public void testDefault(int deptId){
		Dept department = new Dept();
		department.setId(deptId);
		department.setName("hello-default");
		sqlManager.insert(department);
		throw new RuntimeException("模拟回滚");

	}

	/**
	 * 容器管理事物，测试数据是否提交
	 * @param deptId
	 * @return
	 */
	@Transactional(rollbackFor = RuntimeException.class,transactionManager="defaultTs")
	public boolean testDefaultCommit(int deptId){
		return sqlManager.exist(Dept.class,deptId);
	}

	private  DataSourceTransactionManager getTS(String dsName){
		DataSourceTransactionManager ts = (DataSourceTransactionManager)context.getBean(dsName+"TS");
		return ts;
	}

	private TransactionStatus create(String dsName){
		DataSourceTransactionManager dataSourceTransactionManager = this.getTS(dsName);
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
		defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transaction = dataSourceTransactionManager.getTransaction(defaultTransactionDefinition);
		return transaction;

	}
	private SQLManager sqlManager(String name){
		return sqlManagerConcurrentHashMap.computeIfAbsent(name, new Function<String, SQLManager>() {
			@Override
			public SQLManager apply(String name) {
				DataSource ds = getDs(name);
				SpringConnectionSource source = new SpringConnectionSource(ds,null);
				SQLManagerBuilder sqlManagerBuilder = new SQLManagerBuilder(source);
				sqlManagerBuilder.setNc(new UnderlinedNameConversion());
				sqlManagerBuilder.setInters(new Interceptor[]{new DebugInterceptor()});
				SQLManager sqlManager =  sqlManagerBuilder.build();
				DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");
				return sqlManager;
			}
		});
	}

	private DataSource getDs(String name){

		return dsMap.computeIfAbsent(name, new Function<String, DataSource>() {
			@Override
			public DataSource apply(String name) {
				HikariDataSource ds = new HikariDataSource();
				ds.setJdbcUrl("jdbc:h2:mem:"+name+";DB_CLOSE_ON_EXIT=FALSE");
				ds.setUsername("sa");
				ds.setPassword("");
				ds.setDriverClassName("org.h2.Driver");
				ds.setMaximumPoolSize(2);
				//注册成springbean
				DynamicDSApplication.context.registerBean(name,DataSource.class, new Supplier<DataSource>() {
					@Override
					public DataSource get() {
						return ds;
					}
				},new BeanDefinitionCustomizer[0]);
				DataSource newDs =  (DataSource)DynamicDSApplication.context.getBean(name) ;


				DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(newDs);
				DynamicDSApplication.context.registerBean(name+"TS",DataSourceTransactionManager.class, new Supplier<DataSourceTransactionManager>() {
					@Override
					public DataSourceTransactionManager get() {
						return transactionManager;
					}
				},new BeanDefinitionCustomizer[0]);
				return  newDs;

			}
		});


	}
}
