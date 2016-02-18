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
public class SelectMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m);
		Map<String,Object> sqlArgs = this.getSqlArgs(sm, entityClass,m, args);
		
		if(desc.paggerPos!=null){
			int offset ,size ;
			offset = ((Number)args[desc.paggerPos[0]]).intValue();
			size = ((Number)args[desc.paggerPos[1]]).intValue();
			return sm.select(sqlId, entityClass, sqlArgs,offset,size);
		}else{
			return sm.select(sqlId, entityClass, sqlArgs);
		}
		
	}

	
}
