package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
/**
 * 指示字段是数据库插入的时候自动生成的，比如通过触发器生成
 * @see  AutoID
 */
public @interface Auto {

}


