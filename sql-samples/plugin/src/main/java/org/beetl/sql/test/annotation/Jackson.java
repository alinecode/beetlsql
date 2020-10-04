package org.beetl.sql.test.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.annotation.builder.UpdateTimeConvert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 把对象序列化成字符串传存入到数据库
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(JacksonConvert.class)
public @interface Jackson {

}
