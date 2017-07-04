package org.beetl.sql.core.mapper.internal;

import java.lang.reflect.Method;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;

/**
 * 生成select * from table where id = ? for update 的行级锁查询语句
 * create time : 2017-6-26 13:04
 *
 * @author darren
 */
public class LockAmi implements MapperInvoke {

	@Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        return sm.lock(entityClass, args[0]);
    }

}
