package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;
/**
 * 使用此可以用于模板引擎，如果对于序列化，所有值都在getTails方法里
 * @author xiandafu
 *
 */
public class TailBean implements Tail {
	protected Map<String,Object> extMap = new HashMap<String,Object>();
	
	public Object get(String key){
		return extMap.get(key);
	}
	
	public void set(String key,Object value){
		this.extMap.put(key, value);
		
	}

	public Map<String, Object> getTails() {
		return extMap;
	}

	
	
	
	
	
	
}
