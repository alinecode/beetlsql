package org.beetl.sql.mapper.lambda;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.mapper.annotation.SubQuery;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 *
 * @author xiandafu
 * @see SubQuery
 */
public class SubQueryBuilder implements MapperExtBuilder {

	@Override
	public MapperInvoke parse(Class entity, Method m) {
		ParameterParser parameterParser = new ParameterParser(m);
		MethodParamsHolder paramsHolder = parameterParser.getHolder();

		String ns = getNamespace(m,entity);
		String id = m.getName();
		SqlId sqlId = SqlId.of(ns,id);

		Type type = m.getGenericReturnType();
		//方法可能是LambdaQuery<OtherEntity> or LambdaQuery<OtherEntity>
		Class targetType = BeanKit.getCollectionType(type);
		if(targetType==null){
			targetType = entity;
		}

		SubQueryMapperInvoke subQueryMapperInvoke = new SubQueryMapperInvoke(sqlId,targetType,paramsHolder);
		return subQueryMapperInvoke;
	}


	protected String getNamespace(Method method,Class entity){
		Class mapperClass = method.getDeclaringClass();
		SqlResource methodSqlResoruce = method.getAnnotation(SqlResource.class);
		if(methodSqlResoruce!=null){
			return methodSqlResoruce.value();
		}
		SqlResource sqlResource =  (SqlResource)mapperClass.getAnnotation(SqlResource.class);
		if(sqlResource!=null){
			return sqlResource.value();
		}
		//从实体名获取
		if(entity!=null){
			String namespace = StringKit.toLowerCaseFirstOne(entity.getSimpleName());
			return namespace;
		}
		throw new BeetlSQLException(BeetlSQLException.MAPPER_ERROR,"需要使用@SqlResource");
	}



}
