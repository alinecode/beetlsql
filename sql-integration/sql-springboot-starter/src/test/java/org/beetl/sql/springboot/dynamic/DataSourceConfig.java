package org.beetl.sql.springboot.dynamic;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.ConditionalSQLManager;
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
public class DataSourceConfig {
    @Autowired
    ApplicationContext ctx;

    @Bean(name = "ds1")
    public DataSource ds1(Environment env) {
		return newDataSource(env,"spring.datasource.url");
    }

    @Bean(name = "ds2")
    public DataSource ds2(Environment env) {
       return newDataSource(env,"spring.datasource.url2");
    }

    protected  DataSource newDataSource(Environment env,String urlKey){
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(env.getProperty(urlKey));
		ds.setUsername(env.getProperty("spring.datasource.username"));
		ds.setPassword(env.getProperty("spring.datasource.password"));
		ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		return ds;
	}


}
