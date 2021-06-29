package org.beetl.sql.core.call;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallResult {
	Map<String,Object>  objs = new HashMap<>();

	public  <T> List<T> getList(String name,Class<T> t){
		return null;
	}

	public <T> T get(String name ,Class<T> t){
		return null;
	}

	public void add(String name,Object value){
		objs.put(name,value);
	}
}
