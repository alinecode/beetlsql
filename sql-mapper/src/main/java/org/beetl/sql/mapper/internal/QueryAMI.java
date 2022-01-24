package org.beetl.sql.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 *
 *
 * @author xiandafu
 */
public class QueryAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		return sm.query(entityClass);
	}

}
