package org.beetl.sql.ext.spring4;

import static org.springframework.util.Assert.notNull;

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

/**
 * 扫描配置，根据配置的信息进行扫描
 * @author woate
 */
public class BeetlSqlScannerConfigurer  implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {
    /**
     * 基本包，用于指定在该基本包路径下进行扫描，可以支持;空格,等分割多个包
     */
    String basePackage;
    
    String daoSuffix="Dao";
    /**
     * Spring上下文
     */
    ApplicationContext applicationContext;
    /**
     * Bean名称
     */
    String beanName;

    BeanNameGenerator nameGenerator;
    /**
     * sqlManagerFactoryBean名称
     */
    String sqlManagerFactoryBeanName;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //将配置中的多个基本包拆分开
        String[] packages = StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
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
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, " 'basePackage' 属性必须配置");
    }

    public BeanNameGenerator getNameGenerator() {
        return nameGenerator;
    }

    public void setNameGenerator(BeanNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setSqlManagerFactoryBeanName(String sqlManagerFactoryBeanName) {
        this.sqlManagerFactoryBeanName = sqlManagerFactoryBeanName;
    }

	public String getDaoSuffix() {
		return daoSuffix;
	}

	public void setDaoSuffix(String daoSuffix) {
		this.daoSuffix = daoSuffix;
	}

    
    
}
