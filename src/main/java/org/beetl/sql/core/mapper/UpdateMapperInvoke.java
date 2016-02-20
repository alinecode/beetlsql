package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.SQLManager;

/**
 *  
 * @author xiandafu
 *
 */
public class UpdateMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		Map<String,Object> sqlArgs = this.getSqlArgs(sm, entityClass,m, args,sqlId);
		return sm.update(sqlId, sqlArgs);
		
	}

	
}
