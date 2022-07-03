package org.beetl.sql.mapper.call;

import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;

import java.lang.reflect.Method;

public class CallBuilder implements MapperExtBuilder {
	@Override
	public MapperInvoke parse(Class entity, Method m) {
		return null;
	}
}
