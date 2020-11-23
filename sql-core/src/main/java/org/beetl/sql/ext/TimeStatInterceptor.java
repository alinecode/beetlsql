package org.beetl.sql.ext;

import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.SQLParameter;

import java.util.HashMap;
import java.util.List;

/** 用来统计sql执行时间
 * @author joelli
 *
 */
public class TimeStatInterceptor implements Interceptor {

	Filter filter = null;
	long max;

	public TimeStatInterceptor(long max) {
		this(null, max);
	}

	public TimeStatInterceptor(Filter filter, long max) {
		this.filter = filter;
		this.max = max;
	}

	@Override
	public void before(InterceptorContext ctx) {
		if (!include(ctx.getExecuteContext().sqlId)) {
			return;
		}
		ctx.setEnv(new HashMap<>());
		ctx.getEnv().put("stat.time", System.currentTimeMillis());

	}

	@Override
	public void after(InterceptorContext ctx) {
		if (!include(ctx.getExecuteContext().sqlId)) {
			return;
		}
		long end = System.currentTimeMillis();
		long start = (Long) ctx.get("stat.time");
		if ((end - start) > max) {
			ExecuteContext executeContext = ctx.getExecuteContext();
			print(executeContext.sqlId.toString(), executeContext.sqlResult.jdbcSql, executeContext.sqlResult.jdbcPara,
					(end - start));
		}


	}


	protected void print(String sqlId, String sql, List<SQLParameter> paras, long time) {
		System.out.println("sqlId=" + sqlId + " time:" + time);
		System.out.println("=====================");
		System.out.println(DebugInterceptor.formatSql(sql));

	}

	protected boolean include(SqlId id) {
		if (filter == null) {
			return true;
		}
		return filter.isAccept(id);
	}

	public static interface Filter {
		public boolean isAccept(SqlId id);
	}

	@Override
	public void exception(InterceptorContext ctx, Exception ex) {

	}

}
