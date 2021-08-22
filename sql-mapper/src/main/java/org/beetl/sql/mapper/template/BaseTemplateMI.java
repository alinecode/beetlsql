package org.beetl.sql.mapper.template;

import org.beetl.sql.core.SqlId;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

import java.lang.reflect.Method;

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


	/**
	 * 使用entity+method 来生成sqlId
	 *
	 * @param c
	 * @param method
	 * @return
	 */
	public SqlId getSqId(Class c , Method method){
		return SqlId.of(c.getName(),method.getName());
	}
}
