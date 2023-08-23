package org.beetl.sql.xml.test;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.StringSqlTemplateLoader;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.starter.SQLManagerCustomize;
import org.beetl.sql.xml.XMLBeetlSQL;
import org.beetl.sql.xml.XMLClasspathLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class SimpleDataSourceConfig {
    @Autowired
    ApplicationContext ctx;

    @Bean(name = "ds1")
    public DataSource datasource(Environment env) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        ds.setMaximumPoolSize(2);
        return ds;
    }



    @Bean
    public SQLManagerCustomize mySQLManagerCustomize(){
        return new SQLManagerCustomize(){
            @Override
            public void customize(String sqlMangerName, SQLManager manager) {
            	//初始化sql，这里也可以对sqlManager进行修改

				DBInitHelper.executeSqlScript(manager,"db/schema.sql");
				XMLBeetlSQL xmlBeetlSQL = new XMLBeetlSQL();
				//使用xml classpath loader
				XMLClasspathLoader xmlClasspathLoader = new XMLClasspathLoader("sql");
				xmlClasspathLoader.setClassLoaderKit(manager.getClassLoaderKit());
				xmlClasspathLoader.setDbStyle(manager.getDbStyle());
				manager.setSqlLoader(xmlClasspathLoader);
				//把xmlClasspathLoader用到beetl模板里
				BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine)manager.getSqlTemplateEngine();
				StringSqlTemplateLoader sqlTemplateLoader = new StringSqlTemplateLoader(xmlClasspathLoader);
				beetlTemplateEngine.getBeetl().getGroupTemplate().setResourceLoader(sqlTemplateLoader);
				//配置beetl模板支持的标签
				xmlBeetlSQL.config(manager);

            }
        };
    }
}
