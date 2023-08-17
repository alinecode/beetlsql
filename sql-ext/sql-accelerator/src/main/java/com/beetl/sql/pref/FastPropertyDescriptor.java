package com.beetl.sql.pref;

import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;

import java.beans.PropertyDescriptor;

public class FastPropertyDescriptor extends PropertyDescriptorWrap {
	BeanPropertyAsm beanPropertyAsm = null;
	public FastPropertyDescriptor(Class c, PropertyDescriptor prop, int i) {
		super(c, prop, i);
		beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(c);
	}
	public void setValue(Object o,Object value){
		beanPropertyAsm.setValue(i,o,value);
	}
}
