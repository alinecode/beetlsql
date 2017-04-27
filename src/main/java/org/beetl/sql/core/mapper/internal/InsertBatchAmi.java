package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;

import java.lang.reflect.Method;
import java.util.List;

/**
 * create time : 2017-04-27 16:09
 *
 * @author luoyizhu@gmail.com
 */
public class InsertBatchAmi implements MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        sm.insertBatch(entityClass, (List) args[0]);
        return null;
    }

}
