package org.beetl.sql.ext.spring;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.ThreadLocalSQLManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;

/**
 * 动态 SqlManager，适用于一个项目有多个不同的数据库,
 * 注意，如果是主从数据库，则使用{@link SqlManagerFactoryBean} 即可，传入不同的主从数据源
 * 通过ThreadLocalSQLManager.locals 来指定sqlManager名字
 *
 * @author xiandafu
 */
public class ThreadLocalSqlManagerFactoryBean
		implements FactoryBean<SQLManager>, InitializingBean, ApplicationListener<ApplicationEvent> {
	protected ThreadLocalSQLManager conditionalSQLManager = null;


	protected HashMap<String, SQLManager> all = new HashMap<>();

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

	}

	@Override
	public SQLManager getObject() throws Exception {
		if (conditionalSQLManager != null) {
			return conditionalSQLManager;
		}

		ThreadLocalSQLManager temp = new ThreadLocalSQLManager( all);
		conditionalSQLManager = temp;
		return conditionalSQLManager;
	}

	@Override
	public Class<?> getObjectType() {
		return SQLManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	@Override
	public void afterPropertiesSet() throws Exception {

	}


	public HashMap<String, SQLManager> getAll() {
		return all;
	}

	public void setAll(HashMap<String, SQLManager> all) {
		this.all = all;
	}
}
