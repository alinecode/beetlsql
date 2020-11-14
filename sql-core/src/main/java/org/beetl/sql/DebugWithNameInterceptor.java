package org.beetl.sql;

import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.EnumKit;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.*;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.core.query.interfacer.QueryExecuteI;
import org.beetl.sql.ext.DebugInterceptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 多数据源下输出数据源信息
 * @author darren xiandafu
 *
 */
public class DebugWithNameInterceptor extends DebugInterceptor {

	protected String formatSqlId(ExecuteContext executeContext){
		SqlId id = executeContext.sqlId;
		String str = id.toString();
		String sql = formatSql(str);
		String sqlManagerName = executeContext.sqlManager.getName();
		if(sql.length()>50){
			return sql.substring(0,50)+"..."+"("+sqlManagerName+")";
		}else{
			return sql+"("+sqlManagerName+")";
		}

	}

}
