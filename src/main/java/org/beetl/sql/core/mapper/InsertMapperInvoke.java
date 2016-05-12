package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;

/**
 *  
 * @author xiandafu
 *
 */
public class InsertMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
		Map<String,Object> sqlArgs = this.getSqlArgs(sm, entityClass,m, args,sqlId);
		KeyHolder keyHolder = null;
		if(desc.keyHolderPos!=-1){
			keyHolder = (KeyHolder)args[desc.keyHolderPos];
		}
		
		sm.insert(sqlId,entityClass, sqlArgs, keyHolder);
		return keyHolder;
				
	}

	
}
