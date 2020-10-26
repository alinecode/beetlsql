package org.beetl.sql.mapper.lambda;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

import java.lang.reflect.Method;

public class SubQueryMapperInvoke extends MapperInvoke {
	SqlId sqlId = null;
	Class targetClass = null;
	MethodParamsHolder holder;
	public SubQueryMapperInvoke(SqlId sqlId,Class targetClass,MethodParamsHolder holder){
		this.sqlId =sqlId;
		this.targetClass = targetClass;
		this.holder = holder;
	}
	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		Object paras = ParameterParser.wrapParasForSQLManager(args,holder);
		return new LambdaSubQuery(sm, targetClass,sqlId,paras);
	}
}
