package org.beetl.sql.mapper.provider;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.SqlTemplateProvider;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;

/**
 * 实现{@code SqlTemplateProvider}
 * @author xiandafu
 */
public class SqlTemplatePagePMI extends BaseSqlTemplatePMI {
    boolean  pageResultRequired;
    public SqlTemplatePagePMI(SqlTemplateProvider sqlProvider, Class targetType, boolean  pageResultRequired, MethodParamsHolder holder ){
        super(sqlProvider,targetType,holder);
        this.pageResultRequired = pageResultRequired;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        String sqlTemplate = getSQLTemplateByProvider(sqlProvider,m,args);
        Object paras = getParas(args);
        PageResult pageResult = sm.executePageQuery(sqlTemplate,targetType,paras,(PageRequest)args[holder.getPageRequestIndex()]);
        if(pageResultRequired){
            return pageResult;
        }else{
            return pageResult.getList();
        }

    }


}
