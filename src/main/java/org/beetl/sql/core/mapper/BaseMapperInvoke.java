package org.beetl.sql.core.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.Param;

/**
 *  @author zhoupan,xiandafu
 *
 */
public abstract class BaseMapperInvoke implements MapperInvoke {

	protected Map getSqlArgs(SQLManager sm,Class entityClass,Method m,Object[] args,String sqlId){
		MethodDesc desc = MethodDesc.getMetodDesc(sm,entityClass,m,sqlId);
		Map<String,Object> sqlArgs = new HashMap<String,Object>();
		for(Entry<String,Integer> entry:desc.parasPos.entrySet()){
			sqlArgs.put(entry.getKey(), args[entry.getValue()]);
		}
		return sqlArgs;
		
	}
	
	

}
