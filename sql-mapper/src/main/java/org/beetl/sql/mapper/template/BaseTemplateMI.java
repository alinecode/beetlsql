package org.beetl.sql.mapper.template;

import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

public abstract  class BaseTemplateMI extends MapperInvoke {
    String templateSql;
    MethodParamsHolder holder;

    public BaseTemplateMI(String templateSql,MethodParamsHolder holder){
        this.templateSql = templateSql;
        this.holder = holder;
    }
    public String getSql() {
        return templateSql;
    }



    public Object getParas(Object[] paras){
       return ParameterParser.wrapParasForSQLManager(paras,holder);
    }
}
