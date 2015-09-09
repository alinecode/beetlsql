package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;

public class TailBean implements Tail {
	protected Map<String,Object> extMap = new HashMap<String,Object>();
	
	public Object get(String key){
		return extMap.get(key);
	}
	
	public void set(String key,Object value){
		this.extMap.put(key, value);
		
	}
	
	
	
}
