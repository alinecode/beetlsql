package org.beetl.sql.ext.spring;

import org.beetl.sql.core.SQLManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.support.DaoSupport;

import static org.springframework.util.Assert.notNull;

/**
 * BeetlSql对工厂Bean的实现用于构建Mapper,一次只对一个接口进行扫描，构建Mapper
 * @param <T>
 * @author woate
 */
public class BeetlSqlMapperFactoryBean<T> extends DaoSupport implements FactoryBean<T> , ApplicationContextAware {
	private Class<T> mapperInterface;
	SQLManager sqlManager;

	ApplicationContext applicationContext;




	public BeetlSqlMapperFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}
	public BeetlSqlMapperFactoryBean() {
	}

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		notNull(this.mapperInterface, " 'mapperInterface' 属性是必须的");
	}

	@Override
	public T getObject() throws Exception {
		SQLManagerLifeCycle sqlManagerLifeCycle = null;
		try{
			//可能有性能问题
			 sqlManagerLifeCycle = applicationContext.getBean(SQLManagerLifeCycle.class);
		}catch (Exception exception){
			//ignore
		}
		if(sqlManagerLifeCycle!=null){
			sqlManagerLifeCycle.beforeMapper(mapperInterface);
		}
		return this.sqlManager.getMapper(mapperInterface);
	}

	@Override
	public Class<T> getObjectType() {
		return this.mapperInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	//在扫描时会注入该属性
	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	public SQLManager getSqlManager() {
		return sqlManager;
	}

	public void setSqlManager(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
