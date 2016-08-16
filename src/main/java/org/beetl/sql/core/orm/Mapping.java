package org.beetl.sql.core.orm;

import java.util.List;

import org.beetl.sql.core.SQLManager;

public abstract class Mapping {
	String tailAttrName;
	MappingKey mappingKey;
	public String getTailAttrName() {
		return tailAttrName;
	}

	public void setTailAttrName(String tailAttrName) {
		this.tailAttrName = tailAttrName;
	}
	
	
	public MappingKey getMappingKey() {
		return mappingKey;
	}

	public void setMappingKey(MappingKey mappingKey) {
		this.mappingKey = mappingKey;
	}

	public abstract void map(List list,SQLManager sm);
}
