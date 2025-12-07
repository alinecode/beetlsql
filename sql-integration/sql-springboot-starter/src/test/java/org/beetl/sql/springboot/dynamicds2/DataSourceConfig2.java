package org.beetl.sql.springboot.dynamicds2;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.springboot.dynamicds.DynamicRoutingDataSource;
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
 * 系统的默认数据库，事务管理，以及debugInterceptor
 */
@Configuration
public class DataSourceConfig2 {
    @Autowired
    ApplicationContext ctx;

    @Bean(name = "default")
    public DataSource datasource(Environment env) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        ds.setMaximumPoolSize(2);
        return ds;
    }

	@Bean(name = "ds1")
	public DynamicRoutingDataSource routing(@Qualifier("default") DataSource ds1) {
		DynamicRoutingDataSourceAndSqlManager ds = new DynamicRoutingDataSourceAndSqlManager();
		ds.setDefaultTargetDataSource(ds1);
		return ds;
	}

	@Bean(name = "defaultTs")
	public DataSourceTransactionManager defaultTs(@Qualifier("ds1") DataSource ds) {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(ds);
		return dataSourceTransactionManager;
	}






	@Bean
	public SQLManagerCustomize mySQLManagerCustomize(@Qualifier("ds1") DynamicRoutingDataSource routingDataSource){
		return new SQLManagerCustomize(){
			@Override
			public void customize(String sqlMangerName, SQLManager manager) {

				manager.setInters(new Interceptor[]{new MyDebug(routingDataSource)});


			}
		};
	}

	public class MyDebug extends DebugInterceptor{
		DynamicRoutingDataSource routingDataSource ;
		public MyDebug(DynamicRoutingDataSource routingDataSource ){
			this.routingDataSource = routingDataSource;
		}
		@Override
		protected String formatSqlId(ExecuteContext executeContext){
			String sql = "DB["+routingDataSource.currentDB()+"]->"+super.formatSqlId(executeContext);
			return sql;

		}
	}





}
