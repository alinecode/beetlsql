package org.beetl.sql.test.jpa;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;
import java.util.Optional;

public class JPASingleAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		Object o =  sm.single(entityClass, args[0]);
		return Optional.of(o);
	}
}
