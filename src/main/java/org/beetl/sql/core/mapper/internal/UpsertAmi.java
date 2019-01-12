package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 * @author Succy
 * create on 2019/1/12
 */
public class UpsertAmi implements MapperInvoke {
    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {

        int result = 0;
        if (args.length == 1) {
            result = sm.upsert(args[0]);
        }
        return result;
    }
}
