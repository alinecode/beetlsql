package org.beetl.sql.mapper.identity;

import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;
/**
 * @author xiandafu
 */
public class UpdateRMI extends BaseRMI {
    Class targetType;
    public UpdateRMI(SqlId sqlId, MethodParamsHolder holder) {
        super(sqlId, holder);
    }

    @Override
    public Object call(SQLManager sm, Class entityClass,  Method m, Object[] args) {
        int ret =  sm.update(sqlId,this.getParas(args));
        return ret;

    }
}
