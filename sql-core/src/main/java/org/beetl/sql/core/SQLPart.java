package org.beetl.sql.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于弥补基于模板更新不足，用来添加基于额外的更新和插入值，如一对一关系
 * 
 * @author xiandafu
 *
 */
public class SQLPart {
	
	public Map<String,Object> getUpdateValue(String colName,Object value){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put(colName, value);
		return map;
	}
	
	public String getSelectSuffix(){
		return null;
	}
}
