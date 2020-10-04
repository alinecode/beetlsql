package org.beetl.sql.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 采用SQL模板
 * @see  Sql
 * @see SqlProvider
 * @see SpringData
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Template {


	/**
	 * 采用这个sql，如update xxx set a = #name# where id = #id#
	 * @return sql
	 */
	String value() default "";



}