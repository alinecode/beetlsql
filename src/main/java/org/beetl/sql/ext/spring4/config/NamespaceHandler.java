package org.beetl.sql.ext.spring4.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 支持Spring Xml标签配置方式
 * @author woate
 */
public class NamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("scan", new BeetlSqlScannerBeanDefinitionParser());
    }

}
