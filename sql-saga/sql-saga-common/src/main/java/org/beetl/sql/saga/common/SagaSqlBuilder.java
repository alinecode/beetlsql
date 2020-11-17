package org.beetl.sql.saga.common;

import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.beetl.sql.saga.common.annotation.SagaUpdateSql;

import java.lang.reflect.Method;

/**
 * 执行注解SegaSql语句
 * @author xiandafu
 * @see SagaUpdateSql
 */
public class SagaSqlBuilder implements MapperExtBuilder {

	@Override
	public MapperInvoke parse(Class entity, Method m) {
		SagaUpdateSql segaSql = m.getAnnotation(SagaUpdateSql.class);
		return new SagaSqlMapperInvoke(segaSql.sql(),segaSql.rollback());
	}

}
