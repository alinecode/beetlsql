package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;

public class Params {
	public static Params get(){
		return new Params();
	}
	Map map = new HashMap();
	public Params add(String name,Object value){
		map.put(name, value);
		return this;
	}
	public Map map(){
		return map;
	}
}
