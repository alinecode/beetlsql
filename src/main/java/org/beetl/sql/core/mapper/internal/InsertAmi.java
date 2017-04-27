package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 * <BR>
 * create time : 2017-04-27 15:51
 *
 * @author luoyizhu@gmail.com
 */
public class InsertAmi implements MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        if (args.length == 1) {
            int ret = sm.insert(args[0]);
            return ret;
        }

        int ret = sm.insert(entityClass, args[0], (Boolean) args[1]);
        return ret;
    }
}
