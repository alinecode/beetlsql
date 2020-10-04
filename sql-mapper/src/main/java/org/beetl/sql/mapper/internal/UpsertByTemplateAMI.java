package org.beetl.sql.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 * @author Succy(1459307744@qq.com)，xiandafu
 * create on 2019/1/12
 */
public class UpsertByTemplateAMI extends MapperInvoke {
    @Override
    public Object call(SQLManager sm, Class entityClass,  Method m, Object[] args) {

        boolean result ;
        if (args.length == 1) {
            result = sm.upsertByTemplate(args[0]);
        }else{
            throw  new IllegalArgumentException("期望一个参数");
        }
        return result;
    }
}
