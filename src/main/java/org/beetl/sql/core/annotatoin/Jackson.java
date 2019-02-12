package org.beetl.sql.core.annotatoin;

import com.fasterxml.jackson.databind.JavaType;
import org.beetl.sql.core.handler.HandlerType;
import org.beetl.sql.core.handler.JsonHandler;
import org.beetl.sql.core.handler.UpdateTimePreHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于属性字段上，在插入或者更新的时候,使用jackson序列化成json，再查询的，再还原成bean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD,ElementType.FIELD})
@Handler(value = JsonHandler.class,accept = {HandlerType.UPDATE,HandlerType.INSERT,HandlerType.SELECT})
public @interface Jackson {


}


