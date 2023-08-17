package org.beetl.sql.clazz.kit;

import lombok.Data;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 *
 */

public interface  PropertyDescriptorWrapFactory {
	PropertyDescriptorWrap make(Class c,PropertyDescriptor prop,int i);
}
