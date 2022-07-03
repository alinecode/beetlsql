package org.beetl.sql.mapper.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.mapper.provider.ProviderMapperExtBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在mapper方法上使用此注解，申明是一个存储过程，并配合Select和Update使用
 * @author xiandafu
 * @see Select  查询
 * @see Update  更新
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Builder(ProviderMapperExtBuilder.class)
public @interface Call {


	/**
	 * 采用这个sql，如update xxx set a = ? where id = ?
	 * @return sql
	 */
	String value() default "";



}
