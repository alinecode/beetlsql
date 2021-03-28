package org.beetl.sql.ext;

import org.beetl.sql.core.*;

/**
 * 简单打印sql语句和参数.
 * @author darren xiandafu
 *
 */
public class SimpleDebugInterceptor extends DebugInterceptor {

	public SimpleDebugInterceptor() {
	}

	protected boolean isSimple(SqlId sqlId) {
		return true;
	}



}
