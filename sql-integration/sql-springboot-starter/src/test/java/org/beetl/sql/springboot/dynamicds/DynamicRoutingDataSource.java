package org.beetl.sql.springboot.dynamicds;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.kit.BeanKit;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements Routing {

	protected Map<Object, Object> dsMap = new ConcurrentHashMap<>();
	protected static ThreadLocal<String> dbThreadLocal = ThreadLocal.withInitial(() -> "");


	public DynamicRoutingDataSource() {
		//延迟加载
		super.setTargetDataSources(new HashMap<>());
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return dbThreadLocal.get();
	}

	public Object currentDB(){
		return dbThreadLocal.get();
	}


	@Override
	public void db(String dbName) {
		if (!checkExist(dbName)) {
			//动态初始化数据库
			this.setTargetDataSources((dsMap));

		}
		dbThreadLocal.set(dbName);
	}

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
		try {
			Field field = BeanKit.getField(AbstractRoutingDataSource.class, "resolvedDataSources");
			field.setAccessible(true);
			field.set(this, targetDataSources);
		} catch (Exception exception) {
			throw new UnsupportedOperationException("spring version error for this routing ", exception);
		}

	}


	private boolean checkExist(String name) {

		boolean exist = dsMap.containsKey(name);
		if (exist) {
			return true;
		}
		dsMap.computeIfAbsent(name, new Function<Object, Object>() {
			@Override
			public Object apply(Object name) {
				HikariDataSource ds = new HikariDataSource();
				ds.setJdbcUrl("jdbc:h2:mem:" + name + ";DB_CLOSE_ON_EXIT=FALSE");
				ds.setUsername("sa");
				ds.setPassword("");
				ds.setDriverClassName("org.h2.Driver");
				ds.setMaximumPoolSize(2);
				InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("db/schema.sql");
				new DBInitTool().executeSqlScript(ds, ins);
				return ds;

			}
		});
		return false;
	}


}

