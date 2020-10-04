package org.beetl.sql.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 * <BR>
 * create time : 2017-04-27 15:51
 *
 * @author luoyizhu@gmail.com
 */
public class InsertAMI extends MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        int ret = sm.insert(args[0]);
        return ret;
    }
}
