package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.para.SelectQueryParamter;

/**
 *  
 * @author xiandafu
 *
 */
public class SelecSingleMapperInvoke implements MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDescBySqlId(sm,entityClass,m,sqlId);
		SelectQueryParamter parameter = (SelectQueryParamter)desc.parameter;
		Map map = (Map)parameter.get(args);
		Class returnType = desc.resultType;
		return sm.selectSingle(sqlId, map,desc.resultType);
		
	}

	
}
