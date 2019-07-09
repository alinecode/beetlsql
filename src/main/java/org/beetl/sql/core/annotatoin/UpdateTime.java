package org.beetl.sql.core.annotatoin;

import org.beetl.sql.core.annotatoin.builder.SampleUpdateTimeBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于属性字段上，在插入或者更新的时候,生成一个当前时间，实现类是UpdateTimePreHandler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(value = SampleUpdateTimeBuilder.class)
public @interface UpdateTime {

}


