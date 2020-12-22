package org.beetl.sql.ext;

import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.EnumKit;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.*;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.core.query.interfacer.QueryExecuteI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
