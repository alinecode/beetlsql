package org.beetl.sql.springboot.dynamicds;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.springboot.simple.Department;
import org.beetl.sql.starter.SQLManagerCustomize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 系统的默认数据库，动态数据库常见在
 */
@Configuration
public class DefaultDataSourceConfig {
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

	@Bean(name = "defaultTs")
	public DataSourceTransactionManager defaultTs(@Qualifier("ds1") DataSource ds) {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(ds);
		return dataSourceTransactionManager;
	}
	@Bean
	public SQLManagerCustomize mySQLManagerCustomize(){
		return new SQLManagerCustomize(){
			@Override
			public void customize(String sqlMangerName, SQLManager manager) {
				//初始化sql，这里也可以对sqlManager进行修改

				manager.addVirtualTable("department",Department.virtual_table);
				DBInitHelper.executeSqlScript(manager,"db/schema.sql");
				manager.refresh();

			}
		};
	}


}
