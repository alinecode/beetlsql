package com.beetl.sql.pref;

public abstract  class BeanPropertyWrite {
	 public abstract  void setValue(int index,Object obj,Object attrValue);
	 protected  void throwException(int index,Object obj){
		 throw new IllegalArgumentException("错误属性@"+index+" class="+obj.getClass());
	 }
}
