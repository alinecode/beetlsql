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
	public Object call(SQLManager sm, Class entityClass, String namespace, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDesc(sm,m);
		Map<String,Object> sqlArgs = this.getSqlArgs(sm, m, args);
		
		if(desc.paggerPos!=null){
			int offset ,size ;
			offset = desc.paggerPos[0];
			size = desc.paggerPos[1];
			return sm.select(namespace+"."+m.getName(), entityClass, sqlArgs,offset,size);
		}else{
			return sm.select(namespace+"."+m.getName(), entityClass, sqlArgs);
		}
		
	}

	
}
