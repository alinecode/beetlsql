package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;

/**
 *  
 * @author xiandafu
 *
 */
public class SelectMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
		Map<String,Object> sqlArgs = this.getSqlArgs(sm, entityClass,m, args,sqlId);
		
		if(desc.paggerPos!=null){
			long offset ,size ;
			offset = ((Number)args[desc.paggerPos[0]]).longValue();
			size = ((Number)args[desc.paggerPos[1]]).longValue();
			return sm.select(sqlId, entityClass, sqlArgs,offset,size);
		}else{
			return sm.select(sqlId, entityClass, sqlArgs);
		}
		
	}

	
}
