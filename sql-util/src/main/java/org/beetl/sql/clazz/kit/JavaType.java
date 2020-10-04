package org.beetl.sql.clazz.kit;

import jdk.nashorn.internal.runtime.regexp.joni.constants.EncloseType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * http://www.cnblogs.com/shishm/archive/2012/01/30/2332142.html
 *
 * @author lijiazhi
 * @author linziguan@live.com 2016-12-08 丰富JavaType功能，可以javaType与jdbcType之间互相转换
 */
public class JavaType {

    /**
     * jdbc type to java jdbc type 对应的java的type，参考JavaSqlTypeHandler和BeanProcessor
     */
    public static Map<Integer, Class<?>> jdbcJavaTypes = new HashMap<Integer, Class<?>>();

    /**
     * javaType对应的sqlType，用于一些无schema 数据库表，驱动
     */
    public static Map<Class<?>, Integer> javaTypeJdbcs = new HashMap<Class<?>, Integer>();
    /**
     * type/*生成java代码
     */
    public static Map<Integer, String> mapping = new HashMap<Integer, String>();

    public static Map<String, Integer> jdbcTypeNames = new HashMap<String, Integer>();
    public static Map<Integer, String> jdbcTypeId2Names = new HashMap<Integer, String>();
    public static int majorJavaVersion = 15;

    static {
        String javaVersion = System.getProperty("java.version");
        if (javaVersion.startsWith("13")) {
            majorJavaVersion = 23;
        } else if (javaVersion.startsWith("12")) {
            majorJavaVersion = 22;
        } else if (javaVersion.startsWith("11")) {
            majorJavaVersion = 21;
        } else if (javaVersion.startsWith("10")) {
            majorJavaVersion = 20;
        } else if (javaVersion.startsWith("9")) {
            majorJavaVersion = 19;
        } else if (javaVersion.startsWith("1.8.")) {
            majorJavaVersion = 18;
        } else if (javaVersion.contains("1.7.")) {
            majorJavaVersion = 17;
        } else if (javaVersion.contains("1.6.")) {
            majorJavaVersion = 16;
        }else if (javaVersion.contains("1.5.")) {
            majorJavaVersion = 15;
        }
        else {
            //不识别版本，认为兼容jdk8
            majorJavaVersion = 18;
        }
    }

    public final static String UNKNOW = "UNKNOW";
    public final static String SPECIAL = "SPECIAL";

    static {
        // 初始化jdbcJavaTypes：
        // -16
        jdbcJavaTypes.put(Integer.valueOf(Types.LONGNVARCHAR), String.class);
        // -15 字符串
        jdbcJavaTypes.put(Integer.valueOf(Types.NCHAR), String.class);
        // -9 字符串
        jdbcJavaTypes.put(Integer.valueOf(Types.NVARCHAR), String.class);
        // -8 字符串
        jdbcJavaTypes.put(Integer.valueOf(Types.ROWID), String.class);
        // -7 布尔
        jdbcJavaTypes.put(Integer.valueOf(Types.BIT), Boolean.class);
        // -6 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.TINYINT), Integer.class);
        // -5 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.BIGINT), Long.class);
        // -4
        jdbcJavaTypes.put(Integer.valueOf(Types.LONGVARBINARY), byte[].class);
        // -3 二进制
        jdbcJavaTypes.put(Integer.valueOf(Types.VARBINARY), byte[].class);
        // -2 二进制
        jdbcJavaTypes.put(Integer.valueOf(Types.BINARY), byte[].class);
        // -1
        jdbcJavaTypes.put(Integer.valueOf(Types.LONGVARCHAR), String.class);
        // 字符串
        // jdbcJavaTypes.put(new Integer(Types.NULL), String.class); // 0
        // 1 字符串
        jdbcJavaTypes.put(Integer.valueOf(Types.CHAR), String.class);
        // 2 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.NUMERIC), BigDecimal.class);
        // 3 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.DECIMAL), BigDecimal.class);
        // 4 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.INTEGER), Integer.class);
        // 5 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.SMALLINT), Integer.class);
        // 6 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.FLOAT), BigDecimal.class);
        // 7 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.REAL), BigDecimal.class);
        // 8 数字
        jdbcJavaTypes.put(Integer.valueOf(Types.DOUBLE), BigDecimal.class);
        // 12 字符串
        jdbcJavaTypes.put(Integer.valueOf(Types.VARCHAR), String.class);
        // 16 布尔
        jdbcJavaTypes.put(Integer.valueOf(Types.BOOLEAN), Boolean.class);
        // jdbcJavaTypes.put(new Integer(Types.DATALINK), String.class); // 70
        // 91 日期
        jdbcJavaTypes.put(Integer.valueOf(Types.DATE), Date.class);
        // 92 日期
        jdbcJavaTypes.put(Integer.valueOf(Types.TIME), Time.class);
        // 93 日期
        jdbcJavaTypes.put(Integer.valueOf(Types.TIMESTAMP), Timestamp.class);
//
//		jdbcJavaTypes.put(Types.TIMESTAMP_WITH_TIMEZONE, Timestamp.class);
//		jdbcJavaTypes.put(Types.TIME_WITH_TIMEZONE, Time.class);
        // 1111 其他类型？
        jdbcJavaTypes.put(Integer.valueOf(Types.OTHER), Object.class);
        // jdbcJavaTypes.put(new Integer(Types.JAVA_OBJECT), Object.class); //
        // 2000
        // jdbcJavaTypes.put(new Integer(Types.DISTINCT), String.class); // 2001
        // jdbcJavaTypes.put(new Integer(Types.STRUCT), String.class); // 2002
        // jdbcJavaTypes.put(new Integer(Types.ARRAY), String.class); // 2003
        // 2004 二进制
        jdbcJavaTypes.put(Integer.valueOf(Types.BLOB), byte[].class);
        // 2005 大文本
        jdbcJavaTypes.put(Integer.valueOf(Types.CLOB), String.class);
        // jdbcJavaTypes.put(new Integer(Types.REF), String.class); // 2006
        // 2009
        jdbcJavaTypes.put(Integer.valueOf(Types.SQLXML), SQLXML.class);
        // 2011 大文本
        jdbcJavaTypes.put(Integer.valueOf(Types.NCLOB), String.class);

        //保留java类型可能对应的sql类型
        for(Map.Entry<Integer,Class<?>> entry:jdbcJavaTypes.entrySet()){
            javaTypeJdbcs.put(entry.getValue(),entry.getKey());
        }
    }

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
        mapping.put(Types.SQLXML, "SQLXML");
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

    static {
        Field[] fields = java.sql.Types.class.getFields();
        for (int i = 0, len = fields.length; i < len; ++i) {
            if (Modifier.isStatic(fields[i].getModifiers())) {
                try {
                    String name = fields[i].getName().toLowerCase();
                    Integer value = (Integer) fields[i].get(java.sql.Types.class);
                    jdbcTypeNames.put(name, value);
                    jdbcTypeId2Names.put(value,name);
                } catch (IllegalArgumentException e) {
                    // 不可能发生
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // 不可能发生
                    e.printStackTrace();
                }
            }
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

    /**
     * 得到一个jdbc对应的java类型
     * @param sqlType
     * @param size
     * @param digit
     * @return
     */
    public static String getType(Integer sqlType, Integer size, Integer digit) {
        String type = mapping.get(sqlType);

        if (SPECIAL.equals(type)) {
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

    public static boolean isJavaNumberType(int jdbcType) {
        Class<?> type = jdbcJavaTypes.get(jdbcType);
        return (type == null) ? false : (Number.class.isAssignableFrom(type));
    }

    public static boolean isJdk8() {
        return majorJavaVersion >= 18;
    }

    public static boolean isBigType(int sqlType){
        return sqlType==Types.BLOB||sqlType==Types.CLOB||sqlType==Types.NCLOB;
    }

}