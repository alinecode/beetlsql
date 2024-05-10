package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * beetlsql 当使用内置的更新语句的时候，会忽略此字段
 * @author xiandafu
 * @see org.beetl.sql.core.db.AbstractDBStyle#genUpdateTemplate 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface UpdateIgnore {

}