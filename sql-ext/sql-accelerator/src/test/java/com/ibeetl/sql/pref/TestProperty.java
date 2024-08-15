package com.ibeetl.sql.pref;


import lombok.SneakyThrows;
import org.beetl.sql.clazz.kit.BeanKit;
import org.junit.Test;

import java.beans.PropertyDescriptor;

public class TestProperty {
	@SneakyThrows
	@Test
	public void print(){
		TestBean testBean = new TestBean();
		testBean.setBytes(new byte[0]);

	}
}
