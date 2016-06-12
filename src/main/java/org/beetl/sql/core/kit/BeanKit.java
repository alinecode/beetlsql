package org.beetl.sql.core.kit;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.annotatoin.Tail;

public class BeanKit {
	private static final Map<Class, Method> tailBeans = new ConcurrentHashMap<Class, Method>();
	private static  Method NULL = null;
	static{
		try {
			NULL = Object.class.getMethod("toString", new Class[]{});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static Method getTailMethod(Class type){
		//如果实现了注解也行@Tail也行
		Method m  = tailBeans.get(type);
		if(m!=null){
			if(m==NULL){
				return null;
			}else{
				return m;
			}
			
		}else{
			Tail an = getTailAnnotation(type);
			if(an==null){
				tailBeans.put(type, NULL);
				return null;
			}
			else{
				 m = BeanKit.tailMethod(type, an.set());
				if(m==null){
					tailBeans.put(type, NULL);
					return null;
				}else{
					tailBeans.put(type, m);
					return m;
				}
				
			}
		}
		
		
	}
	
	private static Tail getTailAnnotation(Class type){
		if(Object.class.isAssignableFrom(type)){
			Tail an = (Tail)type.getAnnotation(Tail.class);
			if(an!=null){
				return an;
			}else{
				Class parent = type.getSuperclass();
				if(parent==null){
					return null;
				}
				return getTailAnnotation(parent);
			}
		}else{
			return null;
		}
		
	}
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
	
	public static Method tailMethod(Class type,String name){
		try {
			Method m = type.getMethod(name, new Class[]{String.class,Object.class});
			return m;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
	public static Map getMapIns(Class  cls){
		if(cls==Map.class){
			return new CaseInsensitiveHashMap();
		}else{
			try {
				return (Map)cls.newInstance();
			} catch (Exception e) {
				return null;
			} 
		}
		
		
	}
	
	public static List getListIns(Class  list){
		if(list==List.class){
			return new ArrayList();
		}else{
			try {
				return (List)list.newInstance();
			} catch (Exception e) {
				return null;
			} 
		}
		
	}
	
	private static class CaseInsensitiveHashMap extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = 9178606903603606031L;
		
		private final Map<String, String> lowerCaseMap = new HashMap<String, String>();

        @Override
        public boolean containsKey(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.containsKey(realKey);
        }

        @Override
        public Object get(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.get(realKey);
        }

        @Override
        public Object put(String key, Object value) {
        	
            /*
             * 保持map和lowerCaseMap同步
             * 在put新值之前remove旧的映射关系
             */
            Object oldKey = lowerCaseMap.put(key.toLowerCase(Locale.ENGLISH), key);
            Object oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                this.put(key, value);
            }
        }

        @Override
        public Object remove(Object key) {
            Object realKey = lowerCaseMap.remove(key.toString().toLowerCase(Locale.ENGLISH));
            return super.remove(realKey);
        }
    }
}
