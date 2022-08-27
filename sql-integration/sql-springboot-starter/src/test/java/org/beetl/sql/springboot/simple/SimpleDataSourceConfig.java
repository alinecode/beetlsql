package org.beetl.sql.springboot.simple;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.StringSqlTemplateLoader;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.starter.SQLManagerCustomize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        ds.setMaximumPoolSize(1);
        return ds;
    }



    @Bean
    public SQLManagerCustomize mySQLManagerCustomize(){
        return new SQLManagerCustomize(){
            @Override
            public void customize(String sqlMangerName, SQLManager manager) {
            	//初始化sql，这里也可以对sqlManager进行修改
				DBInitHelper.executeSqlScript(manager,"db/schema.sql");
				//演示一个虚拟表
				manager.addVirtualTable("department",Department.virtual_table);
				BeetlTemplateEngine templateEngine = (BeetlTemplateEngine)manager.getSqlTemplateEngine();
				// 注册一个方法来实现映射到多表的逻辑
				templateEngine.getBeetl().getGroupTemplate().registerFunction("toTable", new Function(){
					@Override
					public Object call(Object[] paras, Context ctx) {
						String tableName = (String)paras[0];
						return tableName;

					}
				});

            }
        };
    }
}
