package org.beetl.sql.core;

public interface QueryResult {
	public Object get(String key);
	public void set(String key,Object value);
}
