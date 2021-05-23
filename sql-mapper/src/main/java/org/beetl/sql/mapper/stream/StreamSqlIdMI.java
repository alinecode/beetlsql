package org.beetl.sql.mapper.stream;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

import java.lang.reflect.Method;
/**
 * <pre>{@code
 *     //user.md#query 提供sql文件模板
 *     StreamData<User> query(Integer status)
 * }</pre>
 */
public class StreamSqlIdMI extends MapperInvoke {
	SqlId sqlId;
	Class targetType;
	MethodParamsHolder holder;

	public StreamSqlIdMI(SqlId sqlId, Class targetType, MethodParamsHolder holder){
		this.sqlId = sqlId;
		this.targetType = targetType;
		this.holder = holder;
	}
	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		Object paras = ParameterParser.wrapParasForSQLManager(args,holder);
		StreamData streamData =  sm.stream(sqlId,targetType,paras);
		return streamData;
	}
}
