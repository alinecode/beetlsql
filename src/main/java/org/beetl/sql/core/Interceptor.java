package org.beetl.sql.core;

public interface Interceptor {
	public void before(InterceptorContext ctx);
	public void after(InterceptorContext ctx);
}
