package org.beetl.sql.ext.spring;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;

/**
 * 动态 SqlManager，适用于一个项目有多个不同的数据库
 * 注意，如果是主从数据库，则使用{@link SqlManagerFactoryBean} 即可，传入不同的主从数据源
 *
 * 动态数据库默认的策略是 {@link ConditionalSQLManager.DefaultConditional},通过操作POJO上是否有{@code @TargetSQLManager}
 * 来决定使用哪个SQLManager
 * @author xiandafu
 */
public class DynamicSqlManagerFactoryBean
		implements FactoryBean<SQLManager>, InitializingBean, ApplicationListener<ApplicationEvent> {
	protected ConditionalSQLManager conditionalSQLManager = null;

	protected ConditionalSQLManager.Conditional conditional = new ConditionalSQLManager.DefaultConditional();
	protected SQLManager defaultSQLManager = null;
	protected HashMap<String, SQLManager> all = new HashMap<>();

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

	}

	@Override
	public SQLManager getObject() throws Exception {
		if (conditionalSQLManager != null) {
			return conditionalSQLManager;
		}

		ConditionalSQLManager temp = new ConditionalSQLManager(defaultSQLManager, all);
		temp.setConditional(conditional);
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

	public ConditionalSQLManager.Conditional getConditional() {
		return conditional;
	}

	public void setConditional(ConditionalSQLManager.Conditional conditional) {
		this.conditional = conditional;
	}

	public SQLManager getDefaultSQLManager() {
		return defaultSQLManager;
	}

	public void setDefaultSQLManager(SQLManager defaultSQLManager) {
		this.defaultSQLManager = defaultSQLManager;
	}

	public HashMap<String, SQLManager> getAll() {
		return all;
	}

	public void setAll(HashMap<String, SQLManager> all) {
		this.all = all;
	}
}
