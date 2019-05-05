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
public class SelectMapperInvoke implements MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDescBySqlId(sm,entityClass,m,sqlId);
		SelectQueryParamter parameter = (SelectQueryParamter)desc.parameter;
		Map map = (Map)parameter.get(args);
		Class returnType = desc.resultType;
		if(parameter.hasRangeSelect()){
			long offset ,size ;
			offset = ((Number)args[parameter.getStartPos()]).longValue();
			size = ((Number)args[parameter.getSizePos()]).longValue();
			return sm.select(sqlId, returnType, map,offset,size);
		}else{
			return sm.select(sqlId, returnType, map);
		}
		
	}

	
}
