package com.beetl.sql.pref;

public abstract  class BeanPropertyAsm {
	public abstract  void setValue(int index,Object obj,Object attrValue);
	public abstract Object getValue(int index,Object obj);
	protected  RuntimeException throwException(int index,Object obj) throws IllegalArgumentException{
		IllegalArgumentException exception =  new IllegalArgumentException(String.valueOf(index));
		return exception;
	}
}
