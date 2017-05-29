package org.beetl.sql.core.mapper;

import org.beetl.sql.core.SQLManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author zhoupan, xiandafu
 */
public abstract class BaseMapperInvoke implements MapperInvoke {

    protected Map getSqlArgs(SQLManager sm, Class entityClass, Method m, Object[] args, String sqlId) {
        MethodDesc desc = MethodDesc.getMetodDesc(sm, entityClass, m, sqlId);
        Map<String, Object> sqlArgs = new HashMap<String, Object>();
        for (Entry<String, Integer> entry : desc.parasPos.entrySet()) {
            sqlArgs.put(entry.getKey(), args[entry.getValue()]);
        }
        return sqlArgs;

    }


}
