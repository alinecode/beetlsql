package org.beetl.sql.ext.spring;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import static org.springframework.util.Assert.notNull;

/**
 * 扫描配置，根据配置的信息进行扫描
 * @author woate
 */
@Data
public class BeetlSqlScannerConfigurer
		implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {
	/**
	 * 基本包，用于指定在该基本包路径下进行扫描，可以支持;空格,等分割多个包
	 */
	protected String basePackage;

	protected String daoSuffix = "Dao";
	/**
	 * Spring上下文
	 */
	protected ApplicationContext applicationContext;
	/**
	 * Bean名称
	 */
	protected String beanName;

	protected BeanNameGenerator nameGenerator;
	/**
	 * sqlManagerFactoryBean名称
	 */
	protected String sqlManagerFactoryBeanName;

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		//将配置中的多个基本包拆分开
		String basePackage2 = this.applicationContext.getEnvironment().resolvePlaceholders(basePackage);
		String[] packages = StringUtils
				.tokenizeToStringArray(basePackage2, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		//创建一个扫描器
		BeetlSqlClassPathScanner scanner = new BeetlSqlClassPathScanner(registry);
		scanner.setSuffix(daoSuffix);

		scanner.setResourceLoader(this.applicationContext);
		scanner.setBeanNameGenerator(this.nameGenerator);
		scanner.setSqlManagerFactoryBeanName(this.sqlManagerFactoryBeanName);
		scanner.registerFilters();
		//对基本包进行扫描，然后调用FactoryBean创建出Mapper
		scanner.doScan(packages);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
			throws BeansException {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(this.basePackage, " 'basePackage' 属性必须配置");
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



}
