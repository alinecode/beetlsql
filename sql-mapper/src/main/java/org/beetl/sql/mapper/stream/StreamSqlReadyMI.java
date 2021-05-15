package org.beetl.sql.mapper.stream;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;
/**
 * <pre>{@code
 *     @Sql("select * from user where status=?")
 *     StreamData<User> query(Integer status)
 * }</pre>
 */
public class StreamSqlReadyMI extends MapperInvoke {
	String sql;
	Class targetType;
	public StreamSqlReadyMI(String sql,Class targetType){
		this.sql = sql;
		this.targetType = targetType;
	}
	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		SQLReady sqlReady = new SQLReady(sql,args);
		StreamData streamData  =  sm.streamExecute(sqlReady,targetType);
		return streamData;
	}
}
