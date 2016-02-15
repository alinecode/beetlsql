package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;

import org.beetl.sql.core.SQLManager;

/**
 * 
 * @author xiandafu
 *
 */
public interface MapperInvoke {
	public Object call(SQLManager sm,Class entityClass,String sqlId,Method m,Object[] args);
}
