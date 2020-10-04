package org.beetl.sql.mapper.provider;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.annotation.SqlTemplateProvider;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 实现{@code SqlTemplateProvider}
 * @author xiandafu
 */
public class SqlTemplatePMI extends BaseSqlTemplatePMI {
    boolean isSelect = false;
    boolean isSingle = false;
    public SqlTemplatePMI(SqlTemplateProvider sqlProvider, Class targetType, MethodParamsHolder holder, boolean isSelect, boolean single){
        super(sqlProvider,targetType,holder);
        this.isSelect = isSelect;
        this.isSingle = single;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        String sqlTemplate = getSQLTemplateByProvider(sqlProvider,m,args);
        if(isSelect){
            List list = sm.execute(sqlTemplate,targetType,getParas(args));
            if(isSingle){
                return list.isEmpty()?null:list.get(0);
            }else{
                return list;
            }
        }else{
            return sm.executeUpdate(sqlTemplate,getParas(args));
        }

    }


}
