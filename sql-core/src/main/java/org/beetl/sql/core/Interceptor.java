package org.beetl.sql.core;

public interface Interceptor {
	void before(InterceptorContext ctx);

	/**
	 * 如果正常执行，调用after
	 * @param ctx
	 */
	void after(InterceptorContext ctx);

	/**
	 *  如果异常，将调用exception
	 * @param ctx
	 * @param ex
	 * @since 2.8.0
	 */
	void exception(InterceptorContext ctx, Exception ex);
}
