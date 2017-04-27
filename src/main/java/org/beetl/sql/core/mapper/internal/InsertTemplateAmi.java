package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 * create time : 2017-04-27 16:10
 *
 * @author luoyizhu@gmail.com
 */
public class InsertTemplateAmi implements MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        if (args.length == 1) {
            int ret = sm.insertTemplate(args[0]);
            return ret;
        }
        
        int ret = sm.insertTemplate(entityClass, args[0], (Boolean) args[1]);
        return ret;
    }
}
