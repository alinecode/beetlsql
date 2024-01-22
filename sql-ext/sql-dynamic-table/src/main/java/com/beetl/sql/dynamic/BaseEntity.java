package com.beetl.sql.dynamic;

import org.beetl.core.fun.MethodInvoker;
import org.beetl.core.fun.ObjectUtil;

/**
 * 动态表格的基础类，用于通过反射访问属性，动态表格可以继承任何基类，这些基类需要提供访问生成的POJO的各个属性
 */
public class BaseEntity {
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
