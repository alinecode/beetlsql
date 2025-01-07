package org.beetl.sql.springboot.dynamicds;

import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.ext.DebugInterceptor;

public class MyDebug extends DebugInterceptor {
	DynamicRoutingDataSource routingDataSource;

	public MyDebug(DynamicRoutingDataSource routingDataSource) {
		this.routingDataSource = routingDataSource;
	}

	@Override
	protected String formatSqlId(ExecuteContext executeContext) {
		String sql = "DB[" + routingDataSource.determineCurrentLookupKey() + "]->" + super.formatSqlId(executeContext);
		return sql;

	}
}
