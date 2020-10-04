package org.beetl.sql.mapper.builder;

import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

public interface MapperConfigBuilder {
    MapperInvoke getAmi(Class entity, Class mapperClass, Method method);

    void addMapperClass(Class c);
}
