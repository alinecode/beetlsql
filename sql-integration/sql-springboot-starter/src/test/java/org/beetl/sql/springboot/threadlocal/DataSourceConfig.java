package org.beetl.sql.springboot.threadlocal;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Autowired
    ApplicationContext ctx;

    @Bean(name = "ds1")
    public DataSource ds1(Environment env) {
		return newDataSource("ds1",env,"spring.datasource.url");
    }

    @Bean(name = "ds2")
    public DataSource ds2(Environment env) {
       return newDataSource("ds2",env,"spring.datasource.url2");
    }

    protected  DataSource newDataSource(String name,Environment env,String urlKey){
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(env.getProperty(urlKey));
		ds.setUsername(env.getProperty("spring.datasource.username"));
		ds.setPassword(env.getProperty("spring.datasource.password"));
		ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        ds.setMaximumPoolSize(2);
        ds.setPoolName(name);
		return ds;
	}


}
