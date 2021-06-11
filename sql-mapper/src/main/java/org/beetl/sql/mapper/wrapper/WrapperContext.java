package org.beetl.sql.mapper.wrapper;

import lombok.Data;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Data
public class WrapperContext {
	SQLManager sqlManager;
	MapperInvoke mapperInvoke;
	Method method;
	Object[] args;
	Annotation config;

}
