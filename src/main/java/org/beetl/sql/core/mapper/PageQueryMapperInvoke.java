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
		Class type = m.getReturnType();
		if(type==void.class||type==PageQuery.class){
			MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
			Class returnType = desc.renturnType;
			sm.pageQuery(sqlId, returnType, (PageQuery)args[0]);
			if(type==PageQuery.class){
				return args[0];
			}else{
				return null;
			}
			
		}else{
			throw new UnsupportedOperationException(m.getName()+" PageQuery查询方法只能返回void或者PageQuery");
		}
		
		
		
	}

	
}
