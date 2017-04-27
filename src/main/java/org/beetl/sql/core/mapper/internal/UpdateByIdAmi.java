package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 * create time : 2017-04-27 16:07
 *
 * @author luoyizhu@gmail.com
 */
public class UpdateByIdAmi implements MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        return sm.updateById(args[0]);
    }

}
