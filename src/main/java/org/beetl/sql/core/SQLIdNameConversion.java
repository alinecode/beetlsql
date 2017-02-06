package org.beetl.sql.core;

import java.lang.reflect.Method;

/**
 * 通过调用方法找到对应的sqlId，以及通过sqlId找到位于resoureLoader下面的文件
 * @author xiandafu
 *
 */
public interface SQLIdNameConversion {
	public String getId(Class z,Method m);
	public String getPath(String sqlId);
}
