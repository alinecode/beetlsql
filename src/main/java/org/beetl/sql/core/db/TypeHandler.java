package org.beetl.sql.core.db;

public interface TypeHandler {
	public Object getDbValue(Object pojoValue);
	public Object getPojoeValue(Object dbValue);
}
