package org.beetl.sql.mapper.ready;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;

/**
 * @author xiandafu
 */
public class PageSqlReadyMI extends BaseSqlReadyMI {
    boolean  pageResultRequired;
    MethodParamsHolder paramsHolder;
    public PageSqlReadyMI(String sql, Class target, boolean pageResultRequired, MethodParamsHolder paramsHolder){
        this.sql = sql;
        this.targetType = target;
        this.pageResultRequired = pageResultRequired;
        this.paramsHolder = paramsHolder;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
       Object[] newArgs = paramsHolder.getArgsExcludePageRequest(args);
       SQLReady sqlReady = new SQLReady(getSqId(entityClass,m),sql,newArgs);
       PageResult pageResult =sm.execute(sqlReady,targetType,(PageRequest)args[paramsHolder.getPageRequestIndex()]);
       if(pageResultRequired){
           return pageResult;
       }else{
           return pageResult.getList();
       }
    }
}
