package org.beetl.sql.mapper.call;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.Call;
import org.beetl.sql.mapper.annotation.Select;
import org.beetl.sql.mapper.annotation.Update;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.beetl.sql.mapper.builder.ReturnTypeParser;

import java.lang.reflect.Method;

public class CallBuilder implements MapperExtBuilder {
	@Override
	public MapperInvoke parse(Class entity, Method m) {
		boolean isUpdate = false;

		Update update = m.getAnnotation(Update.class);

		if(update!=null){
			isUpdate = true;
		}

		Call call = m.getAnnotation(Call.class);

		String callSql= call.value();
		if(StringKit.isBlank(callSql)){
			throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,"@Call注解 缺少sql");
		}

		ReturnTypeParser returnTypeParser = new ReturnTypeParser(m,entity);
		Class queryType = returnTypeParser.isCollection()?returnTypeParser.getCollectionType():returnTypeParser.getType();

		CallParameterParser callParameterParser = new CallParameterParser(m);
		InConfig inConfig = callParameterParser.getInConfig();
		OutBeanConfig outBeanConfig = callParameterParser.getOutBeanConfig();

		CallMapperSelectInvoke callMapperSelectInvoke = new CallMapperSelectInvoke(callSql
				,queryType,inConfig,outBeanConfig,isUpdate,!returnTypeParser.isCollection());

		return callMapperSelectInvoke;

	}
}
