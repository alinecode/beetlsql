package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;

import org.beetl.sql.core.SQLManager;
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
				sm.insert(args[0]);
				return null;
			}else{
				sm.insert(entityClass,args[0],(KeyHolder)args[1]);
				return null;
			}
		}else if(name.equals("insertRenturnKey")){
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
			if(args.length==0){
				return sm.all(entityClass);
			}else{
				return sm.all(entityClass,(Integer)args[0],(Integer)args[1]);
			}
			
		}else if(name.equals("allCount")){
			return sm.allCount(entityClass);
		}else if(name.equals("template")){
			if(args.length==0){
				return sm.template(args[0]);
			}else{
				return sm.template(entityClass,(Integer)args[0],(Integer)args[1]);
			}
			
		}else if(name.equals("templateCount")){
			return sm.templateCount(args[0]);
		}else{
			throw new UnsupportedOperationException(m.getName());
		}
		
	}

	
}
