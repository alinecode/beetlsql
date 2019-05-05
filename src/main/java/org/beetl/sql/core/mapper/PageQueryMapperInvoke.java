package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.para.PageQueryParamter;

/**
 *  
 * @author xiandafu
 *
 */
public class PageQueryMapperInvoke implements MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		MethodDesc desc = MethodDesc.getMetodDescBySqlId(sm,entityClass,m,sqlId);
		PageQueryParamter parameter = (PageQueryParamter)desc.parameter;
		Class returnType = m.getReturnType();
		PageQuery query = (PageQuery)parameter.get(args);
		sm.pageQuery(sqlId, desc.resultType, query);
		if(returnType==PageQuery.class){
			return query;
		}else{
			return null;
		}
		
		
	}

	
}
