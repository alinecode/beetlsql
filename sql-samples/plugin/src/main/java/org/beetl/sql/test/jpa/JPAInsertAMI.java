package org.beetl.sql.test.jpa;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

public class JPAInsertAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		sm.insert(args[0]);
		return args[0];
	}
}
