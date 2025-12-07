package org.beetl.sql.starter;

import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.MapperBuilder;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.ThreadLocalSQLManager;
import org.beetl.sql.ext.spring.DynamicSqlManagerFactoryBean;
import org.beetl.sql.mapper.DefaultMapperBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 动态 SqlManager，适用于一个项目有多个不同的数据库
 * 注意，如果是主从数据库，只需要一个SQLManager，传入不同的主从数据源
 *
 * 动态数据库默认的策略是 根
 *
 * 与Spring的DynamicSqlManagerFactoryBean不同，属性接受BeanName，而不是Bean的引用
 *
 * @author xiandafu
 * @see DynamicSqlManagerFactoryBean
 */
public class ThreadLocalSqlManagerFactoryBean4Sb
		implements FactoryBean<SQLManager>, InitializingBean, ApplicationListener<ApplicationEvent>, ApplicationContextAware
{
	protected ThreadLocalSQLManager conditionalSQLManager = null;


	protected List<String> all = new ArrayList<>();
	protected ApplicationContext applicationContext;
	protected String name;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
	}

	@Override
	public SQLManager getObject() throws Exception {
		if (conditionalSQLManager != null) {
			return conditionalSQLManager;
		}


		HashMap<String, SQLManager> allManager = new HashMap<>();
		SQLManager defaultSQLManager = null;
		for(String sqlManagerName:all){
			SQLManager sqlManager = applicationContext.getBean(sqlManagerName,SQLManager.class);
			if(defaultSQLManager==null){
				defaultSQLManager = sqlManager;
			}
			allManager.put(sqlManagerName,sqlManager);

		}
		MapperBuilder mapperBuilder = new DefaultMapperBuilder();
		ClassLoaderKit classLoaderKit = new ClassLoaderKit();
		ThreadLocalSQLManager temp  = new ThreadLocalSQLManager(defaultSQLManager,allManager,mapperBuilder,classLoaderKit);
		temp.setName(name);
		temp.register();
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



	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



	public List<String> getAll() {
		return all;
	}

	public void setAll(List<String> all) {
		this.all = all;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
