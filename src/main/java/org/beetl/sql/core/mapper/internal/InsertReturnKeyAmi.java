package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 * create time : 2017-04-27 16:06
 *
 * @author luoyizhu@gmail.com
 */
public class InsertReturnKeyAmi implements MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        KeyHolder holder = new KeyHolder();
        sm.insert(entityClass,args[0],holder);
        return holder;
    }

}
