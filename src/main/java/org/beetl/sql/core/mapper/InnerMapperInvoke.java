package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.List;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.db.KeyHolder;

/**
 *   内置的api调用,处理BaseMapper
 * @author xiandafu
 *
 */
public class InnerMapperInvoke extends BaseMapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String namespace, Method m, Object[] args) {
		
		String name = m.getName();
		if(name.equals("insert")){
			if(args.length==1){
				int ret = sm.insert(args[0]);
				return ret;
			}else{
				int ret = sm.insert(entityClass,args[0], (Boolean)args[1]);
				return ret;
			}
		}else if(name.equals("insertReturnKey")){
			KeyHolder holder = new KeyHolder();
			sm.insert(entityClass,args[0],holder);
			return holder;
		}else if(name.equals("updateById")){
			return sm.updateById(args[0]);
		}else if(name.equals("updateTemplateById")){
			return sm.updateTemplateById(args[0]);
		}else if(name.equals("deleteById")){
			return sm.deleteById(entityClass, args[0]);
		}else if(name.equals("unique")){
			return sm.unique(entityClass, args[0]);
		}else if(name.equals("all")){
			if(args==null){
				return sm.all(entityClass);
			}else{
				return sm.all(entityClass,(Integer)args[0],(Integer)args[1]);
			}
			
		}else if(name.equals("allCount")){
			return sm.allCount(entityClass);
		}else if(name.equals("template")){
			if(args.length==1){
				return sm.template(args[0]);
			}else{
				return sm.template(args[0],(Integer)args[1],(Integer)args[2]);
			}
			
		}else if(name.equals("templateCount")){
			return sm.templateCount(args[0]);
		}else if(name.equals("updateByIdBatch")){
			return sm.updateByIdBatch((List<?>)args[0]);
		}else if(name.equals("execute")){
			return sm.execute(new SQLReady((String)args[0],(Object[])args[1]), entityClass);
		}else if(name.equals("executeUpdate")){
			return sm.executeUpdate(new SQLReady((String)args[0],(Object[])args[1]));
		}
		
		else{
			throw new UnsupportedOperationException(m.getName());
		}
		
	}

	
}
