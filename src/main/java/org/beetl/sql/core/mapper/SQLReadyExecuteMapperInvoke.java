package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.List;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;

/**
 *   执行jdbc sql
 * @author xiandafu
 *
 */
public class SQLReadyExecuteMapperInvoke extends BaseMapperInvoke {
	int type ;
	public SQLReadyExecuteMapperInvoke(int type){
		this.type = type;
	}
	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		if(type==2||type==3){
			
			List list = sm.execute(new SQLReady(sqlId,args),entityClass);
			if(type==2){
				return list.size()==0?null:list.get(0);
			}else{
				
				return list;
			}
		}else{
			return sm.executeUpdate(new SQLReady(sqlId,args));
		}
		
				
	}

	
}
