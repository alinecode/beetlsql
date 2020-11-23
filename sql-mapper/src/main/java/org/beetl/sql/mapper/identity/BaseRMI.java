package org.beetl.sql.mapper.identity;

import org.beetl.sql.core.SqlId;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

/**
 * sql来源于sql资源文件,通常mapper的泛型的类名+方法名即sqlId
 * @author xiandafu
 */
public abstract  class BaseRMI extends MapperInvoke {
    SqlId sqlId;
    MethodParamsHolder holder;
    public BaseRMI(SqlId sqlId, MethodParamsHolder holder){
        this.sqlId = sqlId;
        this.holder = holder;
    }
    public Object getParas(Object[] paras){
        return ParameterParser.wrapParasForSQLManager(paras,holder);
    }
}
