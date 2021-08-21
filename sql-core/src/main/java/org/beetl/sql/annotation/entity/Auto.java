package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示字段是数据库插入的时候自动生成的，比如通过触发器生成。如果属性有值，则使用属性的值
 * @author xiandafu
 * @see AutoID
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface Auto {

}


