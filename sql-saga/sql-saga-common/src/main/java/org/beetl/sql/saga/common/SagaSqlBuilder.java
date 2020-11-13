package org.beetl.sql.saga.common;

import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.beetl.sql.saga.common.annotation.SegaUpdateSql;

import java.lang.reflect.Method;

/**
 * 执行注解SegaSql语句
 * @author xiandafu
 * @see SegaUpdateSql
 */
public class SagaSqlBuilder implements MapperExtBuilder {

	@Override
	public MapperInvoke parse(Class entity, Method m) {
		SegaUpdateSql segaSql = m.getAnnotation(SegaUpdateSql.class);
		return new SagaSqlMapperInvoke(segaSql.sql(),segaSql.rollback());
	}

}
