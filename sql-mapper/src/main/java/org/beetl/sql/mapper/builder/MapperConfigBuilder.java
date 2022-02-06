package org.beetl.sql.mapper.builder;

import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

public interface MapperConfigBuilder {
	/**
	 * 获取接口方法对应的{@link MapperInvoke}实现
	 *
	 * @param entity 继承{@code BaseMapper&lt;T&gt;} 接口时给定的泛型T实体类型
	 * @param mapperClass BaseMapper接口或者继承BaseMapper的接口
	 * @param method 调用的接口方法
	 * @return {@link MapperInvoke}
	 */
	MapperInvoke getAmi(Class entity, Class mapperClass, Method method);

	/**
	 * 从实现BaseMapper的接口的所有方法中找@AutoMapper注解指定的MapperInvoke实现并添加
	 *
	 * @param c BaseMapper接口或者继承BaseMapper的接口
	 */
	void addMapperClass(Class c);
}
