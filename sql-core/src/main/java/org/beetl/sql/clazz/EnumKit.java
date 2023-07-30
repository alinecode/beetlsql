package org.beetl.sql.clazz;

import org.beetl.sql.annotation.entity.EnumMapping;
import org.beetl.sql.annotation.entity.EnumValue;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.Cache;
import org.beetl.sql.clazz.kit.DefaultCache;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 记录下实体类中存在的枚举字段与结果集映射的配置信息
 * {@link EnumMapping} 和 {@link EnumValue}
 * @author xiandafu
 */
public class EnumKit {

	/*所有遇到的枚举类的缓存*/
	private static final Cache<Class, EnumConfig> cache = new DefaultCache<>();


	/**
	 * 获得枚举，根据EnumMapping 注解，如果没有，则使用枚举名称
	 * @param c   枚举类
	 * @param value  参考值
	 * @return 枚举
	 */
	public static Enum getEnumByValue(Class c, Object value) {
		if (value == null) {
			return null;
		}
		if (!c.isEnum()) {
			throw new IllegalArgumentException(c.getName());
		}

		EnumConfig config = cache.get(c);
		if (config == null) {
			config = init(c);
		}

		//测试 SQLServer 数据库的 tinyint 类型 会被转为 Short 而如果封装时Key的类型为Integer 则无法取出
		if (Short.class == value.getClass()) {
			value = ((Short) value).intValue();
		}
		return config.map.get(value);
	}

	/**
	 * 得到枚举类在数据库中表达的值
	 * @param en
	 * @return
	 */
	public static Object getValueByEnum(Object en) {
		if (en == null) {
			return null;
		}
		Class c = en.getClass();
		EnumConfig config = cache.get(c);
		if (config == null) {
			config = init(c);
		}
		return config.dbMap.get(en);
	}

	/**
	 * 初始化枚举的映射，如果，则默认按照枚举的名字来，即value是一个字符串
	 * @param c
	 * @see ClassAnnotation
	 */
	public static EnumConfig init(Class c) {
		EnumConfig config = cache.get(c);
		if (config != null) {
			return config;
		}
		String valueAttrName = lookupEnumValueAttr(c);
		if (valueAttrName != null) {
			config = init(c, valueAttrName);
		} else {
			config = initDefaultValue(c);
		}
		return config;

	}

	/**
	 * 自定义枚举类与的数据库数字关系
	 * @param c
	 * @param config
	 */
	public static void init(Class c,EnumConfig config) {
		cache.putIfAbsent(c,config);

	}


	/**
	 * 使用枚举名称作为值
	 * @param c
	 * @return
	 */
	private static EnumConfig initDefaultValue(Class c) {

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
		return config;
	}

	/**
	 * 初始化枚举的映射，使用属性p来作为映射
	 * @param entityClass
	 * @param attr
	 */
	public static EnumConfig init(Class entityClass, String attr) {
		EnumConfig enumConfig = cache.get(entityClass);
		if (enumConfig != null) {
			return enumConfig;
		}

		try {
			PropertyDescriptor[] ps = BeanKit.propertyDescriptors(entityClass);
			for (PropertyDescriptor p : ps) {
				if (p.getName().equals(attr)) {
					enumConfig = init(entityClass, p);
					return enumConfig;
				}
			}
			throw new RuntimeException("在" + entityClass.getCanonicalName() + "中无法找到字段 " + attr);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}


	}


	/**
	 * 查找可能包含EnumValue的字段
	 * @param enumClass
	 * @return
	 */
	private static String lookupEnumValueAttr(Class enumClass) {
		PropertyDescriptor[] ps = null;
		try {
			ps = BeanKit.propertyDescriptors(enumClass);
		} catch (IntrospectionException e) {
			throw new IllegalStateException(e);
		}

		for (PropertyDescriptor p : ps) {
			Method readMethod = p.getReadMethod();
			if (readMethod.getDeclaringClass() == Object.class) {
				continue;
			}
			String attr = p.getName();
			EnumValue enumValue = BeanKit.getAnnotation(enumClass, attr, readMethod, EnumValue.class);
			if (enumValue != null) {
				return attr;
			}
		}

		return null;

	}


	/**
	 * 初始化枚举的映射，使用属性p来作为映射
	 * @param entityClass
	 * @param p
	 */
	private static EnumConfig init(Class entityClass, PropertyDescriptor p) {

		try {
			Method m = p.getReadMethod();
			Map<Object, Enum> map = new HashMap<Object, Enum>();
			Map<Enum, Object> map2 = new HashMap(); // db
			Enum[] temporaryConstants = getEnumValues(entityClass);
			for (Enum e : temporaryConstants) {

				Object key = m.invoke(e);
				map.put(key, e);
				map2.put(e, key);
			}
			EnumConfig config = new EnumConfig(map, map2);
			cache.put(entityClass, config);
			return config;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	/**
	 * 枚举名称和值的对应关系
	 */
	public static class EnumConfig {
		/**
		 * 一般用于数据库获取的值转成java的枚举映射
		 */
		Map<Object, Enum> map;
		/**
		 * 一般用于从java的枚举转数据库字段
		 */
		Map<Enum, Object> dbMap;

		public EnumConfig(Map<Object, Enum> map, Map<Enum, Object> dbMap) {
			this.map = map;
			this.dbMap = dbMap;
		}

	}

	private static Enum[] getEnumValues(Class c) {
		try {
			final Method values = c.getMethod("values");
			java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
				@Override
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

	public static void main(String[] args) {
		Color c = Color.RED;
		Object value = EnumKit.getValueByEnum(c);
		System.out.println(value);

		String a = "BLUE";
		Color e = (Color) EnumKit.getEnumByValue(Color.class, 1);
		System.out.println(e);

	}

	public enum Color {
		RED("RED", 1), BLUE("BLUE", 2);
		private String name;
		private int value;

		Color(String name, int value) {
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
