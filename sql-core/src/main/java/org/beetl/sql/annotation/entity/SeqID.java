package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定主键，且主键对应数据库序列，如果属性有值，则使用属性的值
 * <pre>
 *{@code
 *  @SeqId(name="xxx_seq")
 *  private Long id
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface SeqID {
	/**
	 * 在具备序列功能的数据库的序列名称
	 */
	String name();
}


