package org.beetl.sql.mapper.stream;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

import java.lang.reflect.Method;

/**
 * <pre>{@code
 *     @Template("select * from user where status=#{status}")
 *     StreamData<User> query(Integer status)
 * }</pre>
 */
public class StreamTemplateSqlMI extends MapperInvoke {
	String sql;
	Class targetType;
	MethodParamsHolder holder ;
	public StreamTemplateSqlMI(String sql, Class targetType, MethodParamsHolder holder){
		this.sql = sql;
		this.targetType = targetType;
		this.holder = holder;
	}

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		Object paras = ParameterParser.wrapParasForSQLManager(args,holder);
		StreamData streamData =  sm.streamExecute(sql,targetType,paras);
		return streamData;
	}
}
