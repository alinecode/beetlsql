package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;

/**
 *  辅助生成Map
 *  <pre>
 *  Map paras = Params.ins().add("name",name).map()
 *  User query = ...
 *  Map paras1 = Params.ins(query).add("maxDate",...).map();
 *  </pre>
 * @author xandafu
 *
 */
public class Params {
	Map map = new HashMap();
	public static Params ins(){
		return new Params();
	}
	public static Params ins(Object  obj){
		Params params = new Params();
		params.map.put("_root", obj);
		return params;
	}
	

	public Params add(String name,Object value){
		map.put(name, value);
		return this;
	}
	
	public Params setMain(Object o){
		map.put("_root", o);
		return this;
	}
	public Map map(){
		return map;
	}
}
