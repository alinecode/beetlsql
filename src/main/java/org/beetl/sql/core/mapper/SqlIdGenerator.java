package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;

/**
 * 通过调用方法找到对应的sqlId
 * @author xiandafu
 *
 */
public interface SqlIdGenerator {
	public String getId(Class z,Method m);
}
