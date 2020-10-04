package org.beetl.sql.mapper.provider;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.SqlProvider;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;

/**
 * 实现{@code SqlProvider},mapper的sql语句由SqlProvider类来提供，
 * @author xiandafu
 */
public class SqlPagePMI extends BaseSqlPMI {
    SqlProvider sqlProvider = null;
    Class targetType = null;
    boolean  pageResultRequired;
    MethodParamsHolder paramsHolder;
    public SqlPagePMI(SqlProvider sqlProvider, Class targetType, boolean  pageResultRequired, MethodParamsHolder paramsHolder){
        this.sqlProvider = sqlProvider;
        this.pageResultRequired = pageResultRequired;
        this.paramsHolder = paramsHolder;
        this.targetType = targetType;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        SQLReady ready = getSQLReadyByProvider(sqlProvider,m,args);
        PageResult pageResult = sm.execute(ready,targetType,(PageRequest)args[paramsHolder.getPageRequestIndex()]);

        if(pageResultRequired){
            return pageResult;
        }else{
            return pageResult.getList();
        }
    }

}
