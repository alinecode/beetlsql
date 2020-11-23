package org.beetl.sql.mapper.identity;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;
/**
 * @author xiandafu
 */
public class PageRMI extends BaseRMI {
    Class targetType = null;
    boolean  pageResultRequired;
    public PageRMI(SqlId sqlId, Class targetType, boolean  pageResultRequired, MethodParamsHolder holder) {
        super(sqlId, holder);
        this.targetType = targetType;
        this.pageResultRequired = pageResultRequired;
    }

    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        PageRequest pageRequest = (PageRequest)args[holder.getPageRequestIndex()];
        PageResult pageResult =  sm.pageQuery(sqlId,targetType,getParas(args),pageRequest);
        if(pageResultRequired){
            return pageResult;
        }else{
            return pageResult.getList();
        }
    }
}
