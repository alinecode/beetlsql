package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;

/**
 *  辅助生成Map
 *  <pre>
 *  Map paras = Params.ins().add("name",name).map
 *  ();
 *  </pre>
 * @author xandafu
 *
 */
public class Params {
	
	public static Params ins(){
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
