package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLManager;

/**
 *  
 * @author xiandafu
 *
 */
public class UpdateBatchMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String namespace, Method m, Object[] args) {
		
		if(args[0] instanceof List){
			return sm.updateBatch(namespace+"."+m.getName(), (List)args[0]);
		}else{
			
			return sm.updateBatch(namespace+"."+m.getName(), (Map<String, Object>[])args[0]);
		}
		
		
	}

	
}
