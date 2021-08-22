package org.beetl.sql.mapper.template;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;
import java.util.List;

public class SelectTemplateMI extends BaseTemplateMI {

    Class targetType;
    boolean isSingle = false;
    public SelectTemplateMI(String sql, Class targetType, MethodParamsHolder holder,boolean isSingle){
        super(sql,holder);
        this.targetType = targetType;
        this.isSingle = isSingle;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        List list = sm.execute(getSqId(sm,entityClass,m),templateSql,targetType,(Object)getParas(args));
        if(isSingle){
            return list.isEmpty()?null:list.get(0);
        }else{
            return list;
        }
    }


}
