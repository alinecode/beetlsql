package org.beetl.sql.springboot.dynamicds2;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.ThreadLocalSQLManager;
import org.beetl.sql.springboot.dynamicds.DBInitTool;
import org.beetl.sql.springboot.dynamicds.DynamicRoutingDataSource;

import java.io.InputStream;
import java.util.function.Function;

public class DynamicRoutingDataSourceAndSqlManager extends DynamicRoutingDataSource {
	@Override
	public void db(String dbName) {
		if (!checkExist(dbName)) {
			//动态初始化数据库
			this.setTargetDataSources((dsMap));

		}
		//同时切换数据源和sqlManager
		dbThreadLocal.set(dbName);
		String dbType = dbType(dbName);
		//每个数据库类型，如mysql，h2，oralce,对应一个sqlMaanger，参考配置 application-dynamic-ds-2.properties
		ThreadLocalSQLManager.locals.set("sqlManager"+dbType);
	}
	protected  String dbType(String dbName){
		if(dbName.startsWith("mysql")){
			return "Mysql";
		}else if(dbName.startsWith("h2")){
			return "H2";
		}else{
			throw new UnsupportedOperationException(dbName);
		}
	}

	protected boolean checkExist(String name) {

		boolean exist = dsMap.containsKey(name);
		if (exist) {
			return true;
		}

		dsMap.computeIfAbsent(name, new Function<Object, Object>() {
			@Override
			public Object apply(Object name) {
				if(name.toString().startsWith("h2")){
					HikariDataSource ds = new HikariDataSource();
					ds.setJdbcUrl("jdbc:h2:mem:" + name + ";DB_CLOSE_ON_EXIT=FALSE");
					ds.setUsername("sa");
					ds.setPassword("");
					ds.setDriverClassName("org.h2.Driver");
					ds.setMaximumPoolSize(2);
					InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("db/schema.sql");
					new DBInitTool().executeSqlScript(ds, ins);
					return ds;
				}else if(name.toString().startsWith("mysql")){
					//模拟mysql数据库
					HikariDataSource ds = new HikariDataSource();
					ds.setJdbcUrl("jdbc:h2:mem:" + name + ";DB_CLOSE_ON_EXIT=FALSE");
					ds.setUsername("sa");
					ds.setPassword("");
					ds.setDriverClassName("org.h2.Driver");
					ds.setMaximumPoolSize(2);
					InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("db/schema.sql");
					new DBInitTool().executeSqlScript(ds, ins);
					return ds;
				}else{
					throw new UnsupportedOperationException("not support "+name);
				}


			}
		});
		return false;
	}
}
