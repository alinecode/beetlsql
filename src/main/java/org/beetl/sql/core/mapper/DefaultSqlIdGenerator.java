package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;

import org.beetl.sql.core.kit.StringKit;
/**
 * 类名首字母小写＋ 方法名
 * @author xiandafu
 *
 */
public class DefaultSqlIdGenerator implements SqlIdGenerator {

	@Override
	public String getId(Class z, Method m) {
		// TODO Auto-generated method stub
		String ns = StringKit.toLowerCaseFirstOne(z.getSimpleName());
		String methodName = m.getName();
		return ns+"."+methodName;
	}

}
