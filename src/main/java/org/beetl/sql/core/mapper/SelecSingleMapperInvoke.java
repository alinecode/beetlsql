package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.para.SelectQueryParamter;

/**
 *  
 * @author xiandafu
 *
 */
public class SelecSingleMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
		SelectQueryParamter parameter = (SelectQueryParamter)desc.parameter;
		Map map = (Map)parameter.get(args);
		Class returnType = desc.renturnType;
		return sm.selectSingle(sqlId, map,m.getReturnType());
		
	}

	
}
