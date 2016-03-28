package org.beetl.sql.core.kit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.annotatoin.EnumMapping;

/**
 * 映射枚举
 * 
 * @author xiandafu
 *
 */
public class EnumKit {
	private static Map<Class, EnumConfig> cache = new HashMap<Class, EnumConfig>();

	/**
	 * 获得枚举，根据EnumMapping 注解，如果没有，则使用枚举名称
	 * @param c   枚举类
	 * @param value  参考值
	 * @return
	 */
	public static Enum getEnumByValue(Class c, Object value) {
		if (!c.isEnum())
			 throw new IllegalArgumentException(c.getName());
		EnumConfig config = cache.get(c);
		if (config == null) {
			
			init(c);
			config = cache.get(c);
		}
		
		Enum e =  config.map.get(value);
		
		return e;
	}
	
	public static Object getValueByEnum(Object en) {
		if(en==null) return null;
		Class c = en.getClass();
		
		EnumConfig config = cache.get(c);
		if (config == null) {
			
			init(c);
			config = cache.get(c);
		}
		
		return config.dbMap.get(en);

	}
	
	private static void initNoAnotation(Class c){
		Map<Object, Enum> map = new HashMap<Object, Enum>();
		Map<Enum, Object> map2 = new HashMap(); // db
		Enum[] temporaryConstants = getEnumValues(c);
		for (Enum e : temporaryConstants) {
			//用enum的名称
			Object key = e.name();
			map.put(key, e);
			map2.put(e, key);
		}
		EnumConfig config = new EnumConfig(map, map2);
		cache.put(c, config);
	}

	private static void init(Class c){
		try {
			EnumMapping db = (EnumMapping) c.getAnnotation(EnumMapping.class);
			if(db==null){
				//todo, use default
				initNoAnotation(c);
			}else{
				String name = db.value();
				String getter = "get" + StringKit.toUpperCaseFirstOne(name);
				Method m = c.getMethod(getter, new Class[] {});
				Map<Object, Enum> map = new HashMap<Object, Enum>();
				Map<Enum, Object> map2 = new HashMap(); // db
				Enum[] temporaryConstants = getEnumValues(c);
				for (Enum e : temporaryConstants) {
					
					Object key = m.invoke(e, new Object[] {});
					map.put(key, e);
					map2.put(e, key);
				}
				EnumConfig config = new EnumConfig(map, map2);
				cache.put(c, config);
			}
			
			
			

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static class EnumConfig {
		Map<Object, Enum> map = new HashMap<Object, Enum>();
		Map<Enum, Object> dbMap = new HashMap(); // db

		public EnumConfig(Map<Object, Enum> map, Map<Enum, Object> dbMap) {
			this.map = map;
			this.dbMap = dbMap;
		}

	}
	
	private static Enum[] getEnumValues(Class c){
		
		try {
			final Method values = c.getMethod("values");
			java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
				public Void run() {
					values.setAccessible(true);
					return null;
				}
			});

			Enum[] temporaryConstants;
			temporaryConstants = (Enum[]) values.invoke(null);
			return temporaryConstants;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		
	}
	
	@EnumMapping("value")
	public enum Color { 
		RED("RED",1),BLUE ("BLUE",2);
		private String name;  
		private int value;
		private Color(String name, int value) {  
		    this.name = name;  
		    this.value = value;  
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}  
		
		
	} 
	public static void main(String[] args){
		Color c = Color.BLUE;
		Object value = EnumKit.getValueByEnum(c);
		System.out.println(value);
		
		String a = "BLUE" ;
		Color e = (Color)EnumKit.getEnumByValue(Color.class, a);
		System.out.println(e);
		
	}
}
