package org.beetl.sql.ext.spring;

import lombok.Data;
import org.beetl.core.Function;
import org.beetl.core.tag.TagFactory;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.core.IDAutoGen;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.core.loader.SQLLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static org.springframework.util.Assert.notNull;

/**
 * SqlManager创建工厂
 * @author woate，xiandafu
 */
@Data
public class SqlManagerFactoryBean
		implements FactoryBean<SQLManager>, InitializingBean, ApplicationListener<ApplicationEvent> , ApplicationContextAware {
	/**
	 * 配置文件地址
	 */
	protected Resource configLocation = null;
	protected String defaultSchema = null;
	/**
	 * BeetlSql数据源
	 */
	protected SpringConnectionSource cs;
	/**
	 * 数据库样式
	 */
	protected DBStyle dbStyle;
	/**
	 * 名称转换样式
	 */
	protected NameConversion nc;
	/**
	 * 拦截器
	 */
	protected Interceptor[] interceptors;

	/**sqlManager名称**/
	protected String name;

	protected boolean dev;
	/**
	 * BeetlSql核心类
	 */
	protected SQLManager sqlManager;
	protected SQLLoader sqlLoader;
	protected Properties extProperties = new Properties();
	protected Map<String, Function> functions = Collections.emptyMap();
	protected Map<String, TagFactory> tagFactorys = Collections.emptyMap();
	protected Map<String, IDAutoGen> idAutoGens = Collections.emptyMap();


	ApplicationContext applicationContext;

	SQLManagerLifeCycle sqlManagerLifeCycle;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

	}

	@Override
	public SQLManager getObject() throws Exception {
		if (sqlManager != null) {
			return sqlManager;
		}

		try{
			sqlManagerLifeCycle = applicationContext.getBean(SQLManagerLifeCycle.class);

		}catch (Exception BeansException){
			//忽略，
		}

		//这里配置拦截器
		if (interceptors == null) {
			interceptors = new Interceptor[0];
		}


		Properties properties = new Properties();

		if (configLocation != null) {
			InputStream in = null;
			try {
				// 如果指定了配置文件，先加载配置文件

				in = configLocation.getInputStream();
				properties.load(in);

			} catch (IOException ex) {
				throw new RuntimeException(ex);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {

					}
					in = null;
				}
			}
		}

		//其他扩展属性
		properties.putAll(extProperties);

		//加载数
		if (sqlLoader == null) {
			sqlLoader = new MarkdownClasspathLoader("sql");
		}

		SQLManagerBuilder builder = new SQLManagerBuilder(cs);
		builder.setBeetlPs(properties);
		builder.setNc(nc);
		builder.setInters(interceptors);
		builder.setDbStyle(dbStyle);
		builder.setSqlLoader(this.sqlLoader);
		builder.setProduct(!dev);
		if (name != null) {
			builder.setName(name);
		}

		if(sqlManagerLifeCycle!=null){
			sqlManagerLifeCycle.customizeBuild(name,builder);
		}

		SQLManager tempSQLManager = builder.build();

		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine) tempSQLManager.getSqlTemplateEngine();

		for (Map.Entry<String, Function> entry : functions.entrySet()) {
			beetlTemplateEngine.getBeetl().getGroupTemplate().registerFunction(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, TagFactory> entry : tagFactorys.entrySet()) {
			beetlTemplateEngine.getBeetl().getGroupTemplate().registerTagFactory(entry.getKey(), entry.getValue());
		}


		for (Map.Entry<String, IDAutoGen> entry : this.idAutoGens.entrySet()) {
			tempSQLManager.addIdAutoGen(entry.getKey(), entry.getValue());
		}

		if(sqlManagerLifeCycle!=null){
			sqlManagerLifeCycle.customize(name,tempSQLManager);
		}
		sqlManager = tempSQLManager;

		return sqlManager;
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
		notNull(cs, "'beetlSqlDataSource'数据源是必须配置的");

	}

	/**
	 * 配置BeetlSql数据源
	 *
	 * @param beetlSqlDataSource 数据源对象
	 */
	public void setCs(SpringConnectionSource beetlSqlDataSource) {
		this.cs = beetlSqlDataSource;
	}

	/**
	 * 指定配置文件路径
	 *
	 * @param configLocation 配置路径
	 */
	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}


	/**
	 * 设置数据库方言
	 *
	 * @param dbStyle 数据库方言
	 */
	public void setDbStyle(DBStyle dbStyle) {
		this.dbStyle = dbStyle;
	}

	/**
	 * 设置名称转换样式
	 *
	 * @param nameConversion 名称转换样式
	 */
	public void setNc(NameConversion nameConversion) {
		this.nc = nameConversion;
	}

	/**
	 * 设置拦截器列表
	 *
	 * @param interceptors
	 */
	public void setInterceptors(Interceptor[] interceptors) {
		this.interceptors = interceptors;
	}

	public boolean isDev() {
		return dev;
	}

	public void setDev(boolean dev) {
		this.dev = dev;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
