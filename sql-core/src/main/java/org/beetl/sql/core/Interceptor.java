package org.beetl.sql.core;

/**
 * 在{@link BaseSQLExecutor} 执行SQL时的环绕拦截器
 */
public interface Interceptor {
	/**
	 * 在查询前执行
	 */
	void before(InterceptorContext ctx);

	/**
	 * 如果正常执行，调用after
	 */
	void after(InterceptorContext ctx);

	/**
	 *  如果异常，将调用exception
	 * @since 2.8.0
	 */
	void exception(InterceptorContext ctx, Exception ex);

	default   String formatSql(String sql) {
		String formatSql =  sql.replaceAll("--.*", "").replaceAll("\\n","").replaceAll("\\s+", " ");
		return formatSql;
	}
}
