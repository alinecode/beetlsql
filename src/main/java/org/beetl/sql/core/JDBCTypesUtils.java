package org.beetl.sql.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * java.sql.Types 至  java类型对应
 * @author linziguan@live.com
 *
 */
public class JDBCTypesUtils {
	 
	 private static Map<String, Integer> jdbcTypes; // Name to value
	 private static Map<Integer, String> jdbcTypeValues; // value to Name
	 private static Map<Integer, Class<?>> jdbcJavaTypes; // jdbc type to java type
	 private static Map< Class<?>,String> defaultJavaTypes; // jdbc type to java type
	 static {
	  jdbcTypes = new HashMap();
	  jdbcTypeValues = new HashMap();
	  jdbcJavaTypes = new HashMap();
	  defaultJavaTypes=new HashMap();
	  Field[] fields = java.sql.Types.class.getFields();
	  for (int i=0, len=fields.length; i<len; ++i) {
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
	  //默认类型
	  defaultJavaTypes.put(String.class, "VARCHAR");
	  defaultJavaTypes.put(Integer.class, "NUMERIC");
	  defaultJavaTypes.put(int.class, "NUMERIC");
	  defaultJavaTypes.put(Long.class, "NUMERIC");
	  defaultJavaTypes.put(long.class, "NUMERIC");
	  defaultJavaTypes.put(Date.class, "TIMESTAMP");
	  defaultJavaTypes.put(byte[].class, "BLOB");
	  
	  // 初始化jdbcJavaTypes：
	  jdbcJavaTypes.put(new Integer(Types.LONGNVARCHAR), String.class);  // -16 字符串
	  jdbcJavaTypes.put(new Integer(Types.NCHAR), String.class);    // -15 字符串
	  jdbcJavaTypes.put(new Integer(Types.NVARCHAR), String.class);   // -9 字符串
	  jdbcJavaTypes.put(new Integer(Types.ROWID), String.class);    // -8 字符串
	  jdbcJavaTypes.put(new Integer(Types.BIT), Boolean.class);    // -7 布尔
	  jdbcJavaTypes.put(new Integer(Types.TINYINT), Byte.class);    // -6 数字
	  jdbcJavaTypes.put(new Integer(Types.BIGINT), Long.class);    // -5 数字 
	  jdbcJavaTypes.put(new Integer(Types.LONGVARBINARY), Blob.class);  // -4 二进制
	  jdbcJavaTypes.put(new Integer(Types.VARBINARY), Blob.class);   // -3 二进制
	  jdbcJavaTypes.put(new Integer(Types.BINARY), Blob.class);    // -2 二进制
	  jdbcJavaTypes.put(new Integer(Types.LONGVARCHAR), String.class);  // -1 字符串
	//  jdbcJavaTypes.put(new Integer(Types.NULL), String.class);    // 0 /
	  jdbcJavaTypes.put(new Integer(Types.CHAR), String.class);    // 1 字符串
	  jdbcJavaTypes.put(new Integer(Types.NUMERIC), BigDecimal.class);  // 2 数字
	  jdbcJavaTypes.put(new Integer(Types.DECIMAL), BigDecimal.class);  // 3 数字
	  jdbcJavaTypes.put(new Integer(Types.INTEGER), Integer.class);   // 4 数字
	  jdbcJavaTypes.put(new Integer(Types.SMALLINT), Short.class);   // 5 数字
	  jdbcJavaTypes.put(new Integer(Types.FLOAT), BigDecimal.class);   // 6 数字
	  jdbcJavaTypes.put(new Integer(Types.REAL), BigDecimal.class);   // 7 数字
	  jdbcJavaTypes.put(new Integer(Types.DOUBLE), BigDecimal.class);  // 8 数字
	  jdbcJavaTypes.put(new Integer(Types.VARCHAR), String.class);   // 12 字符串
	  jdbcJavaTypes.put(new Integer(Types.BOOLEAN), Boolean.class);   // 16 布尔
	//  jdbcJavaTypes.put(new Integer(Types.DATALINK), String.class);   // 70 /
	  jdbcJavaTypes.put(new Integer(Types.DATE), Date.class);    // 91 日期
	  jdbcJavaTypes.put(new Integer(Types.TIME), Date.class);    // 92 日期
	  jdbcJavaTypes.put(new Integer(Types.TIMESTAMP), Date.class);   // 93 日期
	  jdbcJavaTypes.put(new Integer(Types.OTHER), Object.class);    // 1111 其他类型？ 
	//  jdbcJavaTypes.put(new Integer(Types.JAVA_OBJECT), Object.class);  // 2000 
	//  jdbcJavaTypes.put(new Integer(Types.DISTINCT), String.class);   // 2001 
	//  jdbcJavaTypes.put(new Integer(Types.STRUCT), String.class);   // 2002 
	//  jdbcJavaTypes.put(new Integer(Types.ARRAY), String.class);    // 2003 
	  jdbcJavaTypes.put(new Integer(Types.BLOB), Blob.class);    // 2004 二进制
	  jdbcJavaTypes.put(new Integer(Types.CLOB), Clob.class);    // 2005 大文本
	//  jdbcJavaTypes.put(new Integer(Types.REF), String.class);    // 2006 
	//  jdbcJavaTypes.put(new Integer(Types.SQLXML), String.class);   // 2009 
	  jdbcJavaTypes.put(new Integer(Types.NCLOB), Clob.class);    // 2011 大文本
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
	 
	 public static String javaTypeToJdbcType(Class c){
	     return defaultJavaTypes.get(c);
	 }
	 
	 public static boolean isJavaNumberType(int jdbcType) {
	  Class<?> type = jdbcJavaTypes.get(jdbcType);
	  return (type == null) ? false : (Number.class.isAssignableFrom(type));
	 }
}
