package org.beetl.sql.springboot.dynamic;

import com.zaxxer.hikari.HikariDataSource;

import org.beetl.sql.DebugWithNameInterceptor;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.saga.common.LocalSagaContextFactory;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaContextFactory;
import org.beetl.sql.saga.kafka.DefaultRollbackCode;
import org.beetl.sql.saga.kafka.KafkaSagaConfig;
import org.beetl.sql.saga.kafka.RollbackCoder;
import org.beetl.sql.starter.SQLManagerCustomize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@ImportAutoConfiguration(KafkaSagaConfig.class)
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


	@Bean
	public SQLManagerCustomize mySQLManagerCustomize(){
		return (sqlMangerName, manager) -> manager.setInters(new Interceptor[]{new DebugWithNameInterceptor()});
	}

	@Bean
	public RollbackCoder rollbackCoder(){
		return new DefaultRollbackCode();
	}



//	@PostConstruct
//	public  void initSaga() {
//    	//必须设置事务实现方式
//		SagaContext.sagaContextFactory = new LocalSagaContextFactory();
//
//	}

}
