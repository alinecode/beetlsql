package org.beetl.sql.sega.common;

import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.beetl.sql.sega.common.annotation.SegaUpdateSql;

import java.lang.reflect.Method;

/**
 * 执行注解SegaSql语句
 * @author xiandafu
 * @see SegaUpdateSql
 */
public class SegaSqlBuilder  implements MapperExtBuilder {

	@Override
	public MapperInvoke parse(Class entity, Method m) {
		SegaUpdateSql segaSql = m.getAnnotation(SegaUpdateSql.class);
		return new SegaSqlMapperInvoke(segaSql.sql(),segaSql.rollback());
	}

}
