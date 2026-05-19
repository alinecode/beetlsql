package org.beetl.sql.clazz.kit;


import java.beans.PropertyDescriptor;

/**
 *
 */

public interface  PropertyDescriptorWrapFactory {
	PropertyDescriptorWrap make(Class c,PropertyDescriptor prop,int i);
}
