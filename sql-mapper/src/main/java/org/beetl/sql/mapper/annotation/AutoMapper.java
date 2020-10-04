package org.beetl.sql.mapper.annotation;

import org.beetl.sql.mapper.MapperInvoke;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标记方法，表示此方法的实现类
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoMapper {



	/**
	 * 用于指定实现
	 * @return MapperInvoke的实现类
	 *
	 */
	Class<? extends MapperInvoke> value() ;


}