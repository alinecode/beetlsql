package org.beetl.sql.ext.spring4.config;

import org.beetl.sql.ext.spring4.BeetlSqlClassPathScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 支持Spring Xml标签配置方式
 * @author woate
 */
public class BeetlSqlScannerBeanDefinitionParser implements BeanDefinitionParser {
    /**
     * 定义用于对要扫描的Dao所在包路径，支持多个包路径
     */
    static String ATTRIBUTE_BASE_PACKAGE = "basePackage";
    static String ATTRIBUTE_DAO_SUFFIX = "daoSuffix";
    static String ATTRIBUTE_SQLMANAGER_FACTORY_BEAN_NAME = "sqlManagerFactoryBeanName";

    @Override
    public synchronized BeanDefinition parse(Element element, ParserContext parserContext) {
        BeetlSqlClassPathScanner scanner = new BeetlSqlClassPathScanner(parserContext.getRegistry());
        ClassLoader classLoader = scanner.getResourceLoader().getClassLoader();
        XmlReaderContext readerContext = parserContext.getReaderContext();
        scanner.setResourceLoader(readerContext.getResourceLoader());
        String sqlManagerFactoryBeanName = element.getAttribute(ATTRIBUTE_SQLMANAGER_FACTORY_BEAN_NAME);
        scanner.setSqlManagerFactoryBeanName(sqlManagerFactoryBeanName);
        String daoSuffix = element.getAttribute(ATTRIBUTE_DAO_SUFFIX);
        scanner.setSuffix(daoSuffix);
        scanner.registerFilters();
        String basePackage = element.getAttribute(ATTRIBUTE_BASE_PACKAGE);
        scanner.scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        return null;
    }

}
