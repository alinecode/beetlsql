package org.beetl.sql.clazz.kit;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 映射枚举
 * 
 * @author xiandafu
 *
 */
public class EnumKit {


	private static Cache<Class, EnumConfig> cache = new DefaultCache<>();

	/**
	 * 获得枚举，根据EnumMapping 注解，如果没有，则使用枚举名称
	 * @param c   枚举类
	 * @param value  参考值
	 * @return 枚举
	 */
	public static Enum getEnumByValue(Class c, Object value) {
		if(value==null){
			return null;
		}
		if (!c.isEnum())
			 throw new IllegalArgumentException(c.getName());

		EnumConfig config = cache.get(c);
		if(config==null){
			//不可能发生
			throw new IllegalStateException("枚举没有初始化");
		}

		//测试 SQLServer 数据库的 tinyint 类型 会被转为 Short 而如果封装时Key的类型为Integer 则无法取出
		if(Short.class == value.getClass()) {
			value = ((Short)value).intValue();
		}
		return config.map.get(value);
	}

	/**
	 * 得到枚举类在数据库中表达的值
	 * @param en
	 * @return
	 */
	public static Object getValueByEnum(Object en) {
		if(en==null) return null;
		Class c = en.getClass();
		EnumConfig config = cache.get(c);
		if(config==null){
			//不可能发生
			throw new IllegalStateException("枚举没有初始化");
		}
		return config.dbMap.get(en);
	}

	/**
	 * 初始化枚举的映射，如果，则默认按照枚举的名字来，即value是一个字符串
	 * @param c
	 * @see ClassAnnotation
	 */
	public static void initNoAnotation(Class c){
		if(cache.get(c)!=null){
			return ;
		}
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

	/**
	 * 初始化枚举的映射，使用属性p来作为映射
	 * @param entityClass
	 * @param p
	 * @see ClassAnnotation
	 */
	public static void init(Class entityClass,PropertyDescriptor p){

		try {
			Method m = p.getReadMethod();
			Map<Object, Enum> map = new HashMap<Object, Enum>();
			Map<Enum, Object> map2 = new HashMap(); // db
			Enum[] temporaryConstants = getEnumValues(entityClass);
			for (Enum e : temporaryConstants) {

				Object key = m.invoke(e, new Object[] {});
				map.put(key, e);
				map2.put(e, key);
			}
			EnumConfig config = new EnumConfig(map, map2);
			cache.put(entityClass, config);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 初始化枚举的映射，使用属性p来作为映射
	 * @param entityClass
	 * @param attr
	 * @see org.beetl.sql.core.db.ClassAnnotation
	 */
	public static void init(Class entityClass,String attr){
		if(cache.get(entityClass)!=null){
			return ;
		}
		try {
			PropertyDescriptor[] ps = BeanKit.propertyDescriptors(entityClass);
			for(PropertyDescriptor p:ps){
				if(p.getName().equals(attr)){
					init(entityClass,p);
					break;
				}
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
	public static void main(String[] args){
		Color c = Color.RED;
		Object value = EnumKit.getValueByEnum(c);
		System.out.println(value);
		
		String a = "BLUE" ;
		Color e = (Color)EnumKit.getEnumByValue(Color.class, 1);
		System.out.println(e);
		
	}
	
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
	
}
