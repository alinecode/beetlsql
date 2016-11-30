package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;

/**
 *  
 * @author xiandafu
 *
 */
public class PageQueryMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
		Class returnType = desc.renturnType==Void.class?entityClass:desc.renturnType;
		sm.pageQuery(sqlId, returnType, (PageQuery)args[0]);
		
		return null;
		
		
	}

	
}
