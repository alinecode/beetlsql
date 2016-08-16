package org.beetl.sql.core.orm;

import org.beetl.sql.core.SQLManager;

public class SimpleMappingKey implements MappingKey{
	String colName;
	public SimpleMappingKey(String colName){
		this.colName = colName;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	@Override
	public Object getValue(Object o,SQLManager sm) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
