package com.beetl.sql.pref;

import org.beetl.sql.clazz.NameConversion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public  class CachedNameConversion  extends NameConversion {

	ConcurrentHashMap<Class, Map<String,String>> cached = new ConcurrentHashMap();
	NameConversion nc ;
	public  CachedNameConversion(NameConversion nc){
		this.nc = nc;
	}

	@Override
	public String getTableName(Class<?> clazz) {
		return nc.getTableName(clazz);
	}

	@Override
	public String getColName(Class<?> c, String attrName) {
		return nc.getColName(c,attrName);
	}

	@Override
	public String getPropertyName(Class<?> c, String colName) {
		Map<String,String> map = cached.get(c);
		if(map==null){
			map = new ConcurrentHashMap<>();
			cached.put(c,map);
		}
		String attrName = map.get(colName);
		if(attrName==null){
			attrName = nc.getPropertyName(c,colName);
			map.put(colName,attrName);
		}

		return attrName;
	}
}
