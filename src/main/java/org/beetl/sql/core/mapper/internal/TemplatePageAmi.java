package org.beetl.sql.core.mapper.internal;

import java.lang.reflect.Method;
import java.util.List;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.MapperInvoke;

/**
 *  用于单表简单翻页查询
 *
 * @author xiandafu
 */
public class TemplatePageAmi implements MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
    		PageQuery query = (PageQuery)args[0];
    		Object obj = query.getParas();
    		
    		if(query.getTotalRow()<0){
    			query.setTotalRow(sm.templateCount(entityClass,obj));
    		}
    		
    		long start = (sm.isOffsetStartZero() ? 0 : 1) + (query.getPageNumber() - 1) * query.getPageSize();
    		long size = query.getPageSize();
    		List<Object> list = null;
    		if(query.getOrderBy()!=null&&query.getOrderBy().trim().length()!=0) {
    		    list = sm.template(entityClass,obj,start, size,query.getOrderBy());
    		}else {
    		    list = sm.template(entityClass,obj,start, size);
    		}
    		query.setList(list);
    		return query;
    		
    }

}
