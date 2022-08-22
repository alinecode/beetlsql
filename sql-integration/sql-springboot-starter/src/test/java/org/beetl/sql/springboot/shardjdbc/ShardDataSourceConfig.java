package org.beetl.sql.springboot.shardjdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.starter.SQLManagerCustomize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ShardDataSourceConfig {
    @Autowired
    ApplicationContext ctx;

//	@Bean(name = "ds0")
//	public DataSource master(Environment env) {
//		HikariDataSource ds = new HikariDataSource();
//		ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
//		ds.setUsername(env.getProperty("spring.datasource.username"));
//		ds.setPassword(env.getProperty("spring.datasource.password"));
//		ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
//		ds.setMaximumPoolSize(1);
//		return ds;
//	}
	@Bean
	public SQLManagerCustomize mySQLManagerCustomize(){
		return new SQLManagerCustomize(){
			@Override
			public void customize(String sqlMangerName, SQLManager manager) {
				//初始化sql，这里也可以对sqlManager进行修改
				DBInitHelper.executeSqlScript(manager,"db/t_order.sql");
				manager.addVirtualTable("t_order0","t_order");
			}
		};
	}


}
