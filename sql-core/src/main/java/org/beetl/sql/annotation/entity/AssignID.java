package org.beetl.sql.annotation.entity;

import org.beetl.sql.core.IDAutoGen;
import org.beetl.sql.ext.SnowflakeIDAutoGen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识字段主键且是程序制定
 * 如果value不为空，则表示某种算法，通过{@link org.beetl.sql.core.SQLManager#addIdAutoGen(String, IDAutoGen)}注册
 * BeetlSQL在插入此对象，会调用此算法获取id值
 * @see SnowflakeIDAutoGen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface AssignID {
	/**
	 * 某个ID算法
	 * @return
	 */
	String value() default "";

	/**
	 * 如果算法有额外的参数需要使用，可以通过此传入，
	 * @return
	 * @see IDAutoGen
	 */
	String param() default "";
}


