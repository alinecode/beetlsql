package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.para.UpdateParamter;

/**
 *  
 * @author xiandafu
 *
 */
public class UpdateMapperInvoke implements MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
		UpdateParamter parameter = (UpdateParamter)desc.parameter;
		Map map = (Map)parameter.get(args);
		return sm.update(sqlId, map);		
	}

	
}
