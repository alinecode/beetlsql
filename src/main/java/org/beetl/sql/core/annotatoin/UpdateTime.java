package org.beetl.sql.core.annotatoin;

import org.beetl.sql.core.handler.HandlerType;
import org.beetl.sql.core.handler.UpdateTimePreHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于属性字段上，在插入或者更新的时候,生成一个当前时间，实现类是UpdateTimePreHandler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD,ElementType.FIELD})
@Handler(value = UpdateTimePreHandler.class,persist=true,select=false)
public @interface UpdateTime {

}


