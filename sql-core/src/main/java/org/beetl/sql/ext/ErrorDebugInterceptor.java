package org.beetl.sql.ext;

import org.beetl.sql.core.InterceptorContext;

/**
 * 同DebugInterceptor,当运行SQL出错时候，才输出详细的SQL执行参数，SQL语句等信息
 */
public class ErrorDebugInterceptor extends DebugInterceptor{
	@Override
	public void after(InterceptorContext ctx) {
		// do nothing
	}

}
