package org.beetl.sql.core.mapper.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    		long totalRow = 0;
    		if(query.getTotalRow()<0){
    		    totalRow = sm.templateCount(entityClass,obj);
    			query.setTotalRow(totalRow);
    		}
    		List<Object> list = null;
    		if(totalRow==0) {
    		    list = new ArrayList(0);
    		}else {
    		    long start = (sm.isOffsetStartZero() ? 0 : 1) + (query.getPageNumber() - 1) * query.getPageSize();
                long size = query.getPageSize();
                if(query.getOrderBy()!=null&&query.getOrderBy().trim().length()!=0) {
                    list = sm.template(entityClass,obj,start, size,query.getOrderBy());
                }else {
                    list = sm.template(entityClass,obj,start, size);
                }
    		}
    		
    		query.setList(list);
    		return query;
    		
    }

}
