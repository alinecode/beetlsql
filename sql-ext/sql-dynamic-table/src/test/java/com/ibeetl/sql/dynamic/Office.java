package com.ibeetl.sql.dynamic;


import org.beetl.core.fun.MethodInvoker;
import org.beetl.core.fun.ObjectUtil;

/**
 * 用户自定义对象
 */
public  class Office  {


	public void setValue(String attrName,Object value){
		MethodInvoker methodInvoker = ObjectUtil.getInvokder(this.getClass(),attrName);
		if(methodInvoker==null){
			throw new IllegalArgumentException("不存在的属性 "+attrName);
		}
		methodInvoker.set(this,value);
	}
	public Object getValue(String attrName){
		MethodInvoker methodInvoker = ObjectUtil.getInvokder(this.getClass(),attrName);
		if(methodInvoker==null){
			throw new IllegalArgumentException("不存在的属性 "+attrName);
		}
		return methodInvoker.get(this);
	}
}
