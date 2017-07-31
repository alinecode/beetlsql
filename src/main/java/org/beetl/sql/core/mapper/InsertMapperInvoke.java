package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.mapper.para.InsertParamter;

/**
 *  
 * @author xiandafu
 *
 */
public class InsertMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
		InsertParamter parameter = (InsertParamter)desc.parameter;
		Map map = (Map)parameter.get(args);
		if(m.getReturnType()==KeyHolder.class){
			KeyHolder holder = new KeyHolder();
			sm.insert(sqlId,entityClass, map, holder);
			return holder;
		}else{
			int ret =  sm.insert(sqlId,entityClass, map,null);
			return ret;
		}
		
				
	}

	
}
