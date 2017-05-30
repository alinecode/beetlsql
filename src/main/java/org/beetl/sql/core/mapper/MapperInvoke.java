package org.beetl.sql.core.mapper;

import org.beetl.sql.core.SQLManager;

import java.lang.reflect.Method;

/**
 * @author xiandafu
 */
public interface MapperInvoke {
    Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args);
}
