package org.beetl.sql.ext.gen;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * http://www.cnblogs.com/shishm/archive/2012/01/30/2332142.html
 * 
 * @author lijiazhi
 * @author linziguan@live.com 
 * 2016-12-08 丰富JavaType功能，可以javaType与jdbcType之间互相转换
 */
public class JavaType {
	private static Map<String, Integer> jdbcTypes; // Name to value
	private static Map<Integer, String> jdbcTypeValues; // value to Name
	private static Map<Integer, Class<?>> jdbcJavaTypes; // jdbc type to java
															// type
	private static Map<Class<?>, String> defaultJavaTypes; // jdbc type to java
															// type

	static int majorJavaVersion = 15;
	static {
		String javaVersion = System.getProperty("java.version");
		// version String should look like "1.4.2_10"
		if (javaVersion.contains("1.9.")) {
			majorJavaVersion = 19;
		} else if (javaVersion.contains("1.8.")) {
			majorJavaVersion = 18;
		} else if (javaVersion.contains("1.7.")) {
			majorJavaVersion = 18;
		} else if (javaVersion.contains("1.6.")) {
			majorJavaVersion = 16;
		} else {
			// else leave 1.5 as default (it's either 1.5 or unknown)
			majorJavaVersion = 15;
		}
	}
	public final static String UNKNOW = "UNKNOW";
	public final static String SPECIAL = "SPECIAL";

	static {
		jdbcTypes = new HashMap();
		jdbcTypeValues = new HashMap();
		jdbcJavaTypes = new HashMap();
		defaultJavaTypes = new HashMap();
		Field[] fields = java.sql.Types.class.getFields();
		for (int i = 0, len = fields.length; i < len; ++i) {
			if (Modifier.isStatic(fields[i].getModifiers())) {
				try {
					String name = fields[i].getName();
					Integer value = (Integer) fields[i].get(java.sql.Types.class);
					jdbcTypes.put(name, value);
					jdbcTypeValues.put(value, name);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		// 默认类型
		defaultJavaTypes.put(String.class, "VARCHAR");
		defaultJavaTypes.put(Integer.class, "NUMERIC");
		defaultJavaTypes.put(int.class, "NUMERIC");
		defaultJavaTypes.put(Long.class, "NUMERIC");
		defaultJavaTypes.put(long.class, "NUMERIC");
		defaultJavaTypes.put(Date.class, "TIMESTAMP");
		defaultJavaTypes.put(byte[].class, "BLOB");

		// 初始化jdbcJavaTypes：
		jdbcJavaTypes.put(new Integer(Types.LONGNVARCHAR), String.class); // -16
																			// 字符串
		jdbcJavaTypes.put(new Integer(Types.NCHAR), String.class); // -15 字符串
		jdbcJavaTypes.put(new Integer(Types.NVARCHAR), String.class); // -9 字符串
		jdbcJavaTypes.put(new Integer(Types.ROWID), String.class); // -8 字符串
		jdbcJavaTypes.put(new Integer(Types.BIT), Boolean.class); // -7 布尔
		jdbcJavaTypes.put(new Integer(Types.TINYINT), Byte.class); // -6 数字
		jdbcJavaTypes.put(new Integer(Types.BIGINT), Long.class); // -5 数字
		jdbcJavaTypes.put(new Integer(Types.LONGVARBINARY), Blob.class); // -4
																			// 二进制
		jdbcJavaTypes.put(new Integer(Types.VARBINARY), Blob.class); // -3 二进制
		jdbcJavaTypes.put(new Integer(Types.BINARY), Blob.class); // -2 二进制
		jdbcJavaTypes.put(new Integer(Types.LONGVARCHAR), String.class); // -1
																			// 字符串
		// jdbcJavaTypes.put(new Integer(Types.NULL), String.class); // 0 /
		jdbcJavaTypes.put(new Integer(Types.CHAR), String.class); // 1 字符串
		jdbcJavaTypes.put(new Integer(Types.NUMERIC), BigDecimal.class); // 2 数字
		jdbcJavaTypes.put(new Integer(Types.DECIMAL), BigDecimal.class); // 3 数字
		jdbcJavaTypes.put(new Integer(Types.INTEGER), Integer.class); // 4 数字
		jdbcJavaTypes.put(new Integer(Types.SMALLINT), Short.class); // 5 数字
		jdbcJavaTypes.put(new Integer(Types.FLOAT), BigDecimal.class); // 6 数字
		jdbcJavaTypes.put(new Integer(Types.REAL), BigDecimal.class); // 7 数字
		jdbcJavaTypes.put(new Integer(Types.DOUBLE), BigDecimal.class); // 8 数字
		jdbcJavaTypes.put(new Integer(Types.VARCHAR), String.class); // 12 字符串
		jdbcJavaTypes.put(new Integer(Types.BOOLEAN), Boolean.class); // 16 布尔
		// jdbcJavaTypes.put(new Integer(Types.DATALINK), String.class); // 70 /
		jdbcJavaTypes.put(new Integer(Types.DATE), Date.class); // 91 日期
		jdbcJavaTypes.put(new Integer(Types.TIME), Date.class); // 92 日期
		jdbcJavaTypes.put(new Integer(Types.TIMESTAMP), Date.class); // 93 日期
		jdbcJavaTypes.put(new Integer(Types.OTHER), Object.class); // 1111 其他类型？
		// jdbcJavaTypes.put(new Integer(Types.JAVA_OBJECT), Object.class); //
		// 2000
		// jdbcJavaTypes.put(new Integer(Types.DISTINCT), String.class); // 2001
		// jdbcJavaTypes.put(new Integer(Types.STRUCT), String.class); // 2002
		// jdbcJavaTypes.put(new Integer(Types.ARRAY), String.class); // 2003
		jdbcJavaTypes.put(new Integer(Types.BLOB), Blob.class); // 2004 二进制
		jdbcJavaTypes.put(new Integer(Types.CLOB), Clob.class); // 2005 大文本
		// jdbcJavaTypes.put(new Integer(Types.REF), String.class); // 2006
		// jdbcJavaTypes.put(new Integer(Types.SQLXML), String.class); // 2009
		jdbcJavaTypes.put(new Integer(Types.NCLOB), Clob.class); // 2011 大文本
	}

	public static Map<Integer, String> mapping = new HashMap<Integer, String>();
	static {
		mapping.put(Types.BIGINT, "Long");
		mapping.put(Types.BINARY, "byte[]");
		mapping.put(Types.BIT, "Integer");
		mapping.put(Types.BLOB, "byte[]");
		mapping.put(Types.BOOLEAN, "Integer");
		mapping.put(Types.CHAR, "String");
		mapping.put(Types.CLOB, "String");
		mapping.put(Types.DATALINK, UNKNOW);
		mapping.put(Types.DATE, "Date");
		mapping.put(Types.DECIMAL, "SPECIAL");
		mapping.put(Types.DISTINCT, UNKNOW);
		mapping.put(Types.DOUBLE, "Double");
		mapping.put(Types.FLOAT, "Float");
		mapping.put(Types.INTEGER, "Integer");
		mapping.put(Types.JAVA_OBJECT, UNKNOW);
		mapping.put(Types.LONGNVARCHAR, "String");
		mapping.put(Types.LONGVARBINARY, "byte[]");
		mapping.put(Types.LONGVARCHAR, "String");
		mapping.put(Types.NCHAR, "String");
		mapping.put(Types.NVARCHAR, "String");
		mapping.put(Types.NCLOB, "String");
		mapping.put(Types.NULL, UNKNOW);
		// 根据长度制定Integer，或者Double
		mapping.put(Types.NUMERIC, SPECIAL);
		mapping.put(Types.OTHER, "Object");
		mapping.put(Types.REAL, "Double");
		mapping.put(Types.REF, UNKNOW);

		mapping.put(Types.SMALLINT, "Integer");
		mapping.put(Types.SQLXML, "String");
		mapping.put(Types.STRUCT, UNKNOW);
		mapping.put(Types.TIME, "Date");
		mapping.put(Types.TIMESTAMP, "Timestamp");
		mapping.put(Types.TINYINT, "Integer");
		mapping.put(Types.VARBINARY, "byte[]");
		mapping.put(Types.VARCHAR, "String");

		// jdk 8 support
		if (majorJavaVersion >= 18) {
			mapping.put(Types.REF_CURSOR, UNKNOW);
			mapping.put(Types.TIMESTAMP_WITH_TIMEZONE, "Timestamp");
			mapping.put(Types.TIME_WITH_TIMEZONE, "Timestamp");
		}

	}

	public static boolean isDateType(Integer sqlType) {
		// 日期类型有特殊操作
		if (sqlType == Types.DATE || sqlType == Types.TIME || sqlType == Types.TIME_WITH_TIMEZONE
				|| sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isInteger(Integer sqlType) {
		if (sqlType == Types.BOOLEAN || sqlType == Types.BIT || sqlType == Types.INTEGER || sqlType == Types.TINYINT
				|| sqlType == Types.SMALLINT) {
			return true;
		} else {
			return false;
		}
	}

	public static String getType(Integer sqlType, Integer size, Integer digit) {
		String type = mapping.get(sqlType);
		if (type.equals(SPECIAL)) {

			if (digit != null && digit != 0) {
				return "Double";
			} else {
				// 有可能是BigInt，但先忽略，这种情况很少，用户也可以手工改
				if (size >= 9) {
					return "Long";
				} else {
					return "Integer";
				}
			}
		} else {
			return type;
		}
	}

	public static int getJdbcCode(String jdbcName) {
		return jdbcTypes.get(jdbcName);
	}

	public static String getJdbcName(int jdbcCode) {
		return jdbcTypeValues.get(jdbcCode);
	}

	public static Class<?> jdbcTypeToJavaType(int jdbcType) {
		return jdbcJavaTypes.get(jdbcType);
	}

	public static String javaTypeToJdbcType(Class c) {
		return defaultJavaTypes.get(c);
	}

	public static boolean isJavaNumberType(int jdbcType) {
		Class<?> type = jdbcJavaTypes.get(jdbcType);
		return (type == null) ? false : (Number.class.isAssignableFrom(type));
	}

}
