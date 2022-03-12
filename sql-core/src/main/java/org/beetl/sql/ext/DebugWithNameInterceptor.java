package org.beetl.sql.ext;

import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * 多数据源下输出数据源信息
 * @author darren xiandafu
 *
 */
public class DebugWithNameInterceptor extends DebugInterceptor {

	@Override
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
