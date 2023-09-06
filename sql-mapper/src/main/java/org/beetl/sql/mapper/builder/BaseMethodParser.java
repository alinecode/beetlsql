package org.beetl.sql.mapper.builder;

import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

public interface BaseMethodParser {
	void init(Class defaultRetType, Class mapperClass, Method method);

	MapperInvoke parse();
}
