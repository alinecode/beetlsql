package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum Color { 
	RED("RED",1),BLUE ("BLUE",2);
	private String name;  
	private int value;
	private Color(String name, int value) {  
	    this.name = name;  
	    this.value = value;  
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}
	/**
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}  
	
} 