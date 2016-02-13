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
public class SelecSingleMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String namespace, Method m, Object[] args) {
		Map<String,Object> sqlArgs = this.getSqlArgs(sm, m, args);
		return sm.selectSingle(namespace+"."+m.getName(),  sqlArgs,m.getReturnType());
		
	}

	
}
