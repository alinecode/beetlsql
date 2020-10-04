package org.beetl.sql.mapper.builder;

import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 *
 * 扩展注解，可以在mapper方法上定义自己的注解来解释执行mapper
 * 开发人员可以利于BeetlSQL的dao框架，扩展任意注解来执行SQL，封装SQL
 *
 * @author xiandafu
 * @see org.beetl.sql.mapper.annotation.SpringData
 * @author xiandafu 
 */
@Plugin
public interface MapperExtBuilder {
    MapperInvoke parse(Class entity, Method m);
}
