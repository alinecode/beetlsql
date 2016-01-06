package org.beetl.sql.ext.gen;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * http://www.cnblogs.com/shishm/archive/2012/01/30/2332142.html
 * @author lijiazhi
 *
 */
public class JavaType {
	public final static  String UNKNOW  = "UNKNOW";
	public final static  String SPECIAL  = "SPECIAL";
	
	public static Map<Integer,String> mapping = new HashMap<Integer,String>();
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
		mapping.put(Types.DECIMAL, "Double");
		mapping.put(Types.DISTINCT, UNKNOW);
		mapping.put(Types.DOUBLE, "Double");
		mapping.put(Types.FLOAT, "Float");
		mapping.put(Types.INTEGER, "Integer");
		mapping.put(Types.JAVA_OBJECT, UNKNOW);
		mapping.put(Types.LONGNVARCHAR, "String");
		mapping.put(Types.LONGVARBINARY, "byte[]");
		mapping.put(Types.LONGVARCHAR, "String");
		mapping.put(Types.NCHAR, "String");
		mapping.put(Types.NCLOB, "String");
		mapping.put(Types.NULL, UNKNOW);
		//根据长度制定Integer，或者Double
		mapping.put(Types.NUMERIC, SPECIAL);
		mapping.put(Types.OTHER, "Object");
		mapping.put(Types.REAL, "Double");
		mapping.put(Types.REF, UNKNOW);
		mapping.put(Types.REF_CURSOR, UNKNOW);
		mapping.put(Types.SMALLINT, "Integer");
		mapping.put(Types.SQLXML, "String");
		mapping.put(Types.STRUCT, UNKNOW);
		mapping.put(Types.TIME, "Date");
		mapping.put(Types.TIME_WITH_TIMEZONE, "Timestamp");
		mapping.put(Types.TIMESTAMP, "Timestamp");
		mapping.put(Types.TIMESTAMP_WITH_TIMEZONE, "Timestamp");
		mapping.put(Types.TINYINT, "Integer");
		mapping.put(Types.VARBINARY, "byte[]");
		mapping.put(Types.VARCHAR, "String");
		
	}
	
	public static boolean isDateType(Integer sqlType){
		//日期类型有特殊操作
		if(sqlType==Types.DATE||sqlType==Types.TIME||sqlType==Types.TIME_WITH_TIMEZONE||sqlType==Types.TIMESTAMP||sqlType==Types.TIMESTAMP_WITH_TIMEZONE){
			return  true ;
		}else{
			return false ;
		}
	}
	
	public static boolean isInteger(Integer sqlType){
		if(sqlType==Types.BOOLEAN||sqlType==Types.BIT||sqlType==Types.INTEGER||sqlType==Types.TINYINT||sqlType==Types.SMALLINT){
			return true ;
		}else{
			return false;
		}
	}
	public static String getType(Integer sqlType,Integer size,Integer digit){
		String type  = mapping.get(sqlType);
		if(type.equals(SPECIAL)){
			
			if(digit!=null){
				return "Double";
			}else{
				//有可能是BigInt，但先忽略，这种情况很少，用户也可以手工改
				if(size>=9){
					return "Long";
				}else{
					return "Integer";
				}
			}
		}else{
			return type ;
		}
	}
	
	
}
