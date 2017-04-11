package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.annotatoin.TableTemplate;
import org.beetl.sql.core.orm.LazyEntity;
/**
 * 使用此可以用于模板引擎，如果对于序列化，所有值都在getTails方法里,TailBean兼容了对LazyEnity的调用
 * @author xiandafu
 *
 */

public class TailBean implements Tail {
	protected Map<String,Object> extMap = new HashMap<String,Object>();
	boolean hasLazy = false ;
	public Object get(String key){
		if(hasLazy){
			Object o = extMap.get(key);;
			if(o instanceof LazyEntity ){
				LazyEntity lazyEntity = (LazyEntity)o;
				try{
					Object real = lazyEntity.get();
					extMap.put(key, real);
					return real;
				}catch(RuntimeException ex){
					throw new BeetlSQLException(BeetlSQLException.ORM_LAZY_ERROR,"Lazy Load Error:"+key+","+ex.getMessage(),ex);
				}
				
				
			}else{
				return o;
			}
		}else{
			return extMap.get(key);
		}
		
	}
	
	public void set(String key,Object value){
		if(value instanceof LazyEntity ){
			hasLazy = true;
		}
		this.extMap.put(key, value);
		
	}

	public Map<String, Object> getTails() {
		 Map<String,Object> newExtMap = new HashMap<String,Object>();
		if(hasLazy){
			for(Entry<String,Object> entry:extMap.entrySet()){
				String key = entry.getKey();
				Object value = entry.getValue();
				if(value instanceof LazyEntity ){
					try{
						LazyEntity lazyEntity = (LazyEntity)value;
						Object real = lazyEntity.get();
						newExtMap.put(key, real);
					}catch(RuntimeException ex){
						throw new BeetlSQLException(BeetlSQLException.ORM_LAZY_ERROR,"Lazy Load Error:"+key+","+ex.getMessage(),ex);
					}
					
				}else{
					newExtMap.put(key, value);
				}
			}
			extMap = newExtMap;
			hasLazy = false;
		}
		
		return extMap;
		
	}

	
	
	
	
	
	
}
