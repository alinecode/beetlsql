package org.beetl.sql.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在mapper方法上使用此注解，申明sql语句来自此注解
 * @author xiandafu
 * @see Template
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sql {


	/**
	 * 采用这个sql，如update xxx set a = ? where id = ?
	 * @return sql
	 */
	String value() default "";



}