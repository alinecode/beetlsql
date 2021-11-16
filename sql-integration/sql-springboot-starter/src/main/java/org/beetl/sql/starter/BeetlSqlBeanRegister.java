package org.beetl.sql.starter;

import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring.BeetlSqlClassPathScanner;
import org.beetl.sql.ext.spring.SqlManagerFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author xiandafu ,waote
 */
public class BeetlSqlBeanRegister
		implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

	private ResourceLoader resourceLoader;
	Environment env;

	BeetlSqlConfig beetlSqlConfig ;


	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		beetlSqlConfig = new BeetlSqlConfig(env);
		this.readySqlManager(registry);


	}



	protected BeanDefinitionBuilder registerBeetlSqlSource(String name,BeetlSqlConfig.SQLManagerConfig config) {
		String sourceConfig = config.getDs();
		String[] sources = sourceConfig.split(",");
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(ConnectionSourceFactory.class);
		bdb.addPropertyValue("masterSource",sources[0]);
		if(sources.length==1){
			return  bdb;
		}
		String[] slaves = new String[sources.length-1];
		for(int i=1 ;i<sources.length;i++){
			slaves[i-1] = sources[i];

		}
		bdb.addPropertyValue("slaveSource", slaves);
		return bdb;
	}

	
	protected void readySqlManager(BeanDefinitionRegistry registry) {
		final ClassLoader classLoader = getClassLoader();
		Map<String, BeetlSqlConfig.SQLManagerConfig> configs =  beetlSqlConfig.getConfigs();
		configs.entrySet().forEach(entry->{
			String sqlManagerName = entry.getKey();
			BeetlSqlConfig.SQLManagerConfig config = entry.getValue();
			if(config.dynamicCondition==null){
				registerSQLManager(registry,sqlManagerName,config,classLoader,true);
			}else{
				registerDynamicSQLManager(registry,sqlManagerName,config,classLoader);
			}


		});
	}



	protected void registerDynamicSQLManager(BeanDefinitionRegistry registry,String name,BeetlSqlConfig.SQLManagerConfig config,ClassLoader classLoader){

		String[] sqlManagers = config.dynamicSqlManager.split(",");
		BeetlSqlConfig.SQLManagerConfig defaultSQLManagerConfig = BeetlSqlConfig.SQLManagerConfig.initDefault(env);

		List<String> managersList = new ArrayList<>();

		for(String sqlManager:sqlManagers){
			BeetlSqlConfig.SQLManagerConfig sqlManagerConfig =
					new BeetlSqlConfig.SQLManagerConfig(env,sqlManager,defaultSQLManagerConfig);
			//初始化每一个sqlManager
			registerSQLManager(registry,sqlManager,sqlManagerConfig,classLoader,false);
			managersList.add(sqlManager);
		}


		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(DynamicSqlManagerFactoryBean4Sb.class);

		bdb.addPropertyValue("all", managersList);
		bdb.addPropertyValue("defaultSQLManager",managersList.get(0));
		bdb.addPropertyValue("conditional", config.dynamicCondition);
		bdb.addPropertyValue("name", name);
		registry.registerBeanDefinition(name, bdb.getBeanDefinition());


		BeetlSqlClassPathScanner scanner = new BeetlSqlClassPathScanner(registry);
		// this check is needed in Spring 3.1
		if (resourceLoader != null) {
			scanner.setResourceLoader(resourceLoader);
		}

		scanner.setSqlManagerFactoryBeanName(name);
		scanner.setSuffix(config.getDaoSuffix());
		scanner.registerFilters();
		scanner.scan(config.getBasePackage().split(","));

	}

	protected  ClassLoader  getClassLoader(){
		 ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader==null) {
			classLoader = this.getClass().getClassLoader();
		}
		return classLoader;
	}


	protected BeanDefinitionBuilder registerSQLManager(BeanDefinitionRegistry registry,String name,BeetlSqlConfig.SQLManagerConfig config,ClassLoader classLoader,boolean  scan){


		MarkdownClasspathLoader loader = new MarkdownClasspathLoader(config.getSqlPath(),config.getSqlFileCharset());
		BeanDefinitionBuilder sqlSourceBuilder = registerBeetlSqlSource(name,config);
		registry.registerBeanDefinition(name+"BeetlSqlDataSourceBean",sqlSourceBuilder.getBeanDefinition());
		Properties ps = new Properties();
		ps.put("PRODUCT_MODE", config.dev?"false":"true");
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(SqlManagerFactoryBean.class);
		bdb.addPropertyValue("cs", new RuntimeBeanReference(name+"BeetlSqlDataSourceBean"));
		bdb.addPropertyValue("dbStyle", (DBStyle)ObjectUtil.tryInstance(config.getDbStyle(),classLoader));
		bdb.addPropertyValue("interceptors", config.dev ? new Interceptor[] { new DebugInterceptor() } : new Interceptor[0]);
		bdb.addPropertyValue("dev", config.dev );

		bdb.addPropertyValue("sqlLoader", loader);
		bdb.addPropertyValue("nc", (NameConversion) ObjectUtil.tryInstance(config.getNameConversion(),classLoader));
		bdb.addPropertyValue("extProperties", ps);
		bdb.addPropertyValue("name", name);


		registry.registerBeanDefinition(name, bdb.getBeanDefinition());
		if(!scan){
			return bdb ;
		}

		BeetlSqlClassPathScanner scanner = new BeetlSqlClassPathScanner(registry);
		// this check is needed in Spring 3.1
		if (resourceLoader != null) {
			scanner.setResourceLoader(resourceLoader);
		}

		scanner.setSqlManagerFactoryBeanName(name);
		scanner.setSuffix(config.getDaoSuffix());
		scanner.registerFilters();
		scanner.scan(config.getBasePackage().split(","));
		return bdb;

	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = env;

	}


}
