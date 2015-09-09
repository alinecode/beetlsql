package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;

/**
 *  辅助生成Map
 *  <pre>
 *  Map paras = Params.start().add("name",name).end();
 *  </pre>
 * @author xandafu
 *
 */
public class Params {
	
	public static Params start(){
		return new Params();
	}
	Map map = new HashMap();

	public Params add(String name,Object value){
		map.put(name, value);
		return this;
	}
	public Map end(){
		return map;
	}
}
