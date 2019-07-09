package org.beetl.sql.ext.spring;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个spring 下多租户数据源的例子，注意，必须调用setMaster，设置一个主数据源，以方便获取数据库元信息
 * @author xiandafu
 *
 */
public class MultiTenantSpringConnectionSourcec extends SpringConnectionSource {
	//当前上下文所属多租户
	static ThreadLocal<String> local = new ThreadLocal<String>();

	//所有的数据源
	Map<String, DataSource> datasources = new HashMap<String, DataSource>();

	@Override
	public Connection getConn(String sqlId, boolean isUpdate, String sql, List paras) {
		String tenant = local.get();
		if (tenant == null) {
			throw new IllegalArgumentException("tenant 为空");
		}

		DataSource ds = datasources.get(tenant);
		if (ds == null) {
			throw new IllegalArgumentException("tenant: " + tenant + " 对应的数据库为空");
		}

		return super.doGetConnectoin(ds);
	}

	public void setCurrentTenant(String tenant) {
		local.set(tenant);
	}

	public Map<String, DataSource> getDatasources() {
		return datasources;
	}

	public void setDatasources(Map<String, DataSource> datasources) {
		this.datasources = datasources;
	}


}
