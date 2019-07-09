package org.beetl.sql.core.annotatoin;

import org.beetl.sql.core.annotatoin.builder.SampleJsonAtrributeBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于属性字段上，在插入或者更新的时候,使用jackson序列化成json，再查询的，再还原成bean
 * 要是此注解正常是哟给你，需要在classpath添加jackson，并且，beetlsql 注解一个jackson函数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(value = SampleJsonAtrributeBuilder.class)
public @interface Jackson {

}


