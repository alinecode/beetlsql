package org.beetlsql.sql.saga.test.spring;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.saga.common.LocalSagaContextFactory;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.starter.SQLManagerCustomize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Autowired
    ApplicationContext ctx;

    @Bean("datasource1")
    public DataSource datasource(Environment env) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));

        return ds;
    }

    @PostConstruct
    public void config(){
        //设置local saga
        SagaContext.sagaContextFactory = new LocalSagaContextFactory();
    }

}
