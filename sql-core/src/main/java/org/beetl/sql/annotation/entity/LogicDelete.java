package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 逻辑删除标记,如果有此标记，那么内置删除语句deleteById 将变成update语句，并设置属性为
 * {@code value}的值
 * @author xiandafu
 * @see org.beetl.sql.core.db.AbstractDBStyle#genDeleteById
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface LogicDelete {
	/**
	 *
	 * @return 设置逻辑删除值
	 */
	int value() default 0;
}


