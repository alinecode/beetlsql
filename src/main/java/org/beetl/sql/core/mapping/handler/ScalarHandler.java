/**  
 * 文件名:    ScalarHandler.java  
 * 描述:      
 * 作者:      suxj
 * 版本:      1.0  
 * 创建时间:  2015年8月2日 下午13:43:10  
 *  
 * 修改历史:  
 * 日期                          作者           版本         描述  
 * ------------------------------------------------------------------  
 * 2015年8月2日        suxj     1.0     1.0 Version  
 */ 
package org.beetl.sql.core.mapping.handler;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.beetl.sql.core.kit.NumberKit;
import org.beetl.sql.core.mapping.ResultSetHandler;

/**  
 * @ClassName: ScalarHandler   
 * @Description: 单值处理器：如select count(*) from user 返回类型为Long
 * @author: suxj  
 * @date:2015年8月2日 下午13:43:10     
 */
public class ScalarHandler<T> implements ResultSetHandler<T> {
	
    private final int columnIndex;
    private final String columnName;
    private final Class<?> requiredType;
    
    public ScalarHandler() {
        this(1, null, null);
    }
    
    public ScalarHandler(Class<T> clazz) {
        this(1, null, clazz);
    }

    public ScalarHandler(int columnIndex, Class<T> clazz) {
        this(columnIndex, null, clazz);
    }

    public ScalarHandler(String columnName, Class<T> clazz) {
        this(1, columnName, clazz);
    }

    private ScalarHandler(int columnIndex, String columnName, Class<T> clazz) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.requiredType = clazz;
    }

	@Override
    public T handle(ResultSet rs) throws SQLException {
    	ResultSetMetaData rsmd = rs.getMetaData();
    	int cloumnCount = rsmd.getColumnCount();
//    	if(cloumnCount != 1){
//    		//TODO 需定义一个异常抛出
//    		throw new SQLException("ResultSet存在多列");
//    	}
    	
    	rs.next();
    	
    	Object result = this.getColumnValue(rs, this.columnIndex, this.requiredType);
		if (result != null && this.requiredType != null && !this.requiredType.isInstance(result)) {
			return (T) convertValueToRequiredType(result, this.requiredType);
		}

        return (T) result;
    	
    }

	/**
	 * 
	 * @MethodName: getColumnValue   
	 * @Description: 获取字段值
	 * @param @param rs
	 * @param @param columnIndex
	 * @param @param requiredType
	 * @param @return
	 * @param @throws SQLException  
	 * @return Object  
	 * @throws
	 */
	private Object getColumnValue(ResultSet rs, int columnIndex, Class<?> requiredType) throws SQLException {
		if(requiredType != null){
			return this.getResultSetValue(rs, columnIndex, requiredType);
		}else{
			return this.getCloumnValue(rs, columnIndex);
		}
	}

	/**
	 * 
	 * @MethodName: getResultSetValue   
	 * @Description: 通过rs.getObject(1)下标 + 期望类型的方式取值    
	 * @param @param rs
	 * @param @param columnIndex
	 * @param @param requiredType
	 * @param @return
	 * @param @throws SQLException  
	 * @return Object  
	 * @throws
	 */
	private Object getResultSetValue(ResultSet rs, int columnIndex, Class<?> requiredType) throws SQLException {
		if(requiredType == null){
			return this.getCloumnValue(rs, columnIndex);
		}
		Object value;

		// Explicitly extract typed value, as far as possible.
		if (String.class == requiredType) {
			return rs.getString(columnIndex);
		}
		else if (boolean.class == requiredType || Boolean.class == requiredType) {
			value = rs.getBoolean(columnIndex);
		}
		else if (byte.class == requiredType || Byte.class == requiredType) {
			value = rs.getByte(columnIndex);
		}
		else if (short.class == requiredType || Short.class == requiredType) {
			value = rs.getShort(columnIndex);
		}
		else if (int.class == requiredType || Integer.class == requiredType) {
			value = rs.getInt(columnIndex);
		}
		else if (long.class == requiredType || Long.class == requiredType) {
			value = rs.getLong(columnIndex);
		}
		else if (float.class == requiredType || Float.class == requiredType) {
			value = rs.getFloat(columnIndex);
		}
		else if (double.class == requiredType || Double.class == requiredType ||
				Number.class == requiredType) {
			value = rs.getDouble(columnIndex);
		}
		else if (BigDecimal.class == requiredType) {
			return rs.getBigDecimal(columnIndex);
		}
		else if (java.sql.Date.class == requiredType) {
			return rs.getDate(columnIndex);
		}
		else if (java.sql.Time.class == requiredType) {
			return rs.getTime(columnIndex);
		}
		else if (java.sql.Timestamp.class == requiredType || java.util.Date.class == requiredType) {
			return rs.getTimestamp(columnIndex);
		}
		else if (byte[].class == requiredType) {
			return rs.getBytes(columnIndex);
		}
		else if (Blob.class == requiredType) {
			return rs.getBlob(columnIndex);
		}
		else if (Clob.class == requiredType) {
			return rs.getClob(columnIndex);
		}
		else {
//			TODO 从jdk7开始，JDBC4.1有getObject(int,class)方法。需要判断是否有这个方法
//			有的话就需要rs.getObject(columnIndex,requiredType)
//			没有的话就原来的方式getColumnValue(rs, columnIndex)
			
			return this.getCloumnValue(rs, columnIndex);
		}

		// Perform was-null check if necessary (for results that the JDBC driver returns as primitives).
		return (rs.wasNull() ? null : value);
	}
	
	/**
	 * 
	 * @MethodName: getCloumnValue   
	 * @Description: 通过下标的方式取值：取值类型为默认类型  
	 * @param @param rs
	 * @param @param columnIndex
	 * @param @return
	 * @param @throws SQLException  
	 * @return Object  
	 * @throws
	 */
	private Object getCloumnValue(ResultSet rs, int columnIndex) throws SQLException {
		Object obj = rs.getObject(columnIndex);
		String className = null;
		if(obj != null){
			className = obj.getClass().getName();
		}
		
		if(obj instanceof java.sql.Blob){
			java.sql.Blob blob = (java.sql.Blob) obj;
			obj = blob.getBytes(1, (int) blob.length());
		}
		else if(obj instanceof java.sql.Clob){
			java.sql.Clob clob = (java.sql.Clob) obj;
			obj = clob.getSubString(1, (int) clob.length());
		}
		else if("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)){
			obj = rs.getTimestamp(columnIndex);
		}
		else if (className != null && className.startsWith("oracle.sql.DATE")) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(columnIndex);
			if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
				obj = rs.getTimestamp(columnIndex);
			}
			else {
				obj = rs.getDate(columnIndex);
			}
		}
		else if (obj != null && obj instanceof java.sql.Date) {
			if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(columnIndex))) {
				obj = rs.getTimestamp(columnIndex);
			}
		}
		return obj;
	}
	
	//TODO 通过rs.getObject("id")列明的方式取值
//	private Object getColumnValue(ResultSet rs, String columnName, Class<?> requiredType) {
//		if(requiredType != null){
//			return this.getResultSetValue(rs, columnName, requiredType);
//		}else{
//			return this.getColumnValue(rs, columnName);
//		}
//	}
	
//	============================辅助类：整理抽取到util中===================================
	
//	AtomicInteger, AtomicLong, BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short 
//	都是java.lang.Number的已知子类
	private Object convertValueToRequiredType(Object result, Class<?> requiredType) {
		if(String.class == requiredType){
			return result.toString();
		}
		//判断Number对象所表示的类或接口是否与requiredType所表示的类或接口是否相同，或者是否是其超类或者超接口
		else if(Number.class.isAssignableFrom(requiredType)){
			if(result instanceof Number){
				return NumberKit.convertNumberToTargetClass(((Number) result), (Class<Number>) requiredType);
			}else{
				return NumberKit.parseNumber(result.toString(), (Class<Number>) requiredType);
			}
		}
		else{
			throw new IllegalArgumentException("无法转化成期望类型");
		}
	}

}
