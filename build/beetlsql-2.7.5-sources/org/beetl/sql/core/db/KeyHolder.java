package org.beetl.sql.core.db;

public class KeyHolder {
	private Object key = null;

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getKey() {
		return key;
	}
	
	public int getInt(){
		return  ((Number)key).intValue();
	}
	
	public long getLong(){
		return  ((Number)key).longValue();
	}
	
	
}
