package org.beetl.sql.core.kit;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BeanKit {
	public static PropertyDescriptor[] propertyDescriptors(Class<?> c) throws IntrospectionException  {

		BeanInfo beanInfo = null;
		beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getPropertyDescriptors();

	}
	
	public static List<Method> getterMethod(Class<?> c)   {

		PropertyDescriptor[] ps;
		try {
			ps = propertyDescriptors(c);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		List<Method> list = new ArrayList<Method>();
		for(PropertyDescriptor p:ps){
			if(p.getReadMethod()!=null&&p.getWriteMethod()!=null){
				list.add(p.getReadMethod());
			}
		}
		return list;
		

	}
}
