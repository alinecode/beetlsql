package com.ibeetl.sql.pref;


import lombok.SneakyThrows;
import org.beetl.sql.clazz.kit.BeanKit;
import org.junit.Test;

import java.beans.PropertyDescriptor;

public class TestProperty {
	@SneakyThrows
	@Test
	public void print(){
		PropertyDescriptor[] propertyDescriptors = BeanKit.propertyDescriptors(TestBean.class);
		int i=0;
		for(PropertyDescriptor ps:propertyDescriptors){
			System.out.println(i++ +" " +ps.getName());
		}

	}
}
