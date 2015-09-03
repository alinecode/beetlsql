/**  
 * 文件名:    BeanProcessor.java  
 * 描述:      
 * 作者:      suxj
 * 版本:      1.0  
 * 创建时间:  2015年8月2日 上午1:00:24  
 *  
 * 修改历史:  
 * 日期                          作者           版本         描述  
 * ------------------------------------------------------------------  
 * 2015年8月2日        suxj     1.0     1.0 Version  
 */
package org.beetl.sql.core.mapping;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.beetl.sql.core.Bean2;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.NameConversion;

/**
 * @ClassName: BeanProcessor
 * @Description: Pojo处理器，负责转换
 * @author: suxj
 * @date:2015年8月2日 上午1:00:24
 */
public class BeanProcessor {

	private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();
	private final Map<String, String> columnToPropertyOverrides;
	protected static final int PROPERTY_NOT_FOUND = -1;
	private NameConversion nc = new HumpNameConversion();

	//基本类型
	static {
		primitiveDefaults.put(Integer.TYPE, Integer.valueOf(0));
		primitiveDefaults.put(Short.TYPE, Short.valueOf((short) 0));
		primitiveDefaults.put(Byte.TYPE, Byte.valueOf((byte) 0));
		primitiveDefaults.put(Float.TYPE, Float.valueOf(0f));
		primitiveDefaults.put(Double.TYPE, Double.valueOf(0d));
		primitiveDefaults.put(Long.TYPE, Long.valueOf(0L));
		primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
		primitiveDefaults.put(Character.TYPE, Character.valueOf((char) 0));
	}

	public BeanProcessor() {
		this(new HashMap<String, String>());//为{} 非null
	}
	
	public BeanProcessor(NameConversion nc) {
		this();
		this.nc = nc;
	}

	public BeanProcessor(Map<String, String> columnToPropertyOverrides) {
		super();
		if (columnToPropertyOverrides == null) {
			throw new IllegalArgumentException("columnToPropertyOverrides map cannot be null");
		}
		this.columnToPropertyOverrides = columnToPropertyOverrides;
	}

	/**
	 * 
	 * @MethodName: toBean   
	 * @Description: 将ResultSet映射为一个POJO对象  
	 * @param @param rs
	 * @param @param type
	 * @param @return
	 * @param @throws SQLException  
	 * @return T  
	 * @throws
	 */
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {

		PropertyDescriptor[] props = this.propertyDescriptors(type);

		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(type,rsmd, props);

		return this.createBean(rs, type, props, columnToProperty);
		
	}

	/**
	 * 
	 * @MethodName: toBeanList   
	 * @Description: 将ResultSet映射为一个List<POJO>集合  
	 * @param @param rs
	 * @param @param type
	 * @param @return
	 * @param @throws SQLException  
	 * @return List<T>  
	 * @throws
	 */
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
		
		List<T> results = new ArrayList<T>();

		if (!rs.next()) {
			return results;
		}

		PropertyDescriptor[] props = this.propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(type,rsmd, props);

		do {
			results.add(this.createBean(rs, type, props, columnToProperty));
		} while (rs.next());

		return results;
		
	}
	
	/**
	 * 
	 * @MethodName: toMap   
	 * @Description: 将rs转化为Map<String ,Object>  
	 * @param @param rs
	 * @param @return
	 * @param @throws SQLException  
	 * @return Map<String,Object>  
	 * @throws
	 */
	public Map<String, Object> toMap(Class<?> c,ResultSet rs) throws SQLException {

		Map<String, Object> result = new CaseInsensitiveHashMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
//		String tableName = nc.getTableName(c);
		for (int i = 1; i <= cols; i++) {
			
			String columnName = rsmd.getColumnLabel(i);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(i);
			}
			
			// 通过ResultSetMetaData类，可判断该列数据类型
			if (columnName.equals("BLOB")) {
				java.sql.Blob bb = rs.getBlob(i);
				byte[] b = bb.getBytes(1, (int) bb.length());

				// 将结果放到Map中
				//TODO 是该放String还是byte[]
				//result.put(this.nc.getPropertyName(c,columnName), new String(b, "utf-8"));
				//result.put(this.nc.getPropertyName(c,columnName), b);
				
				//rs.getObject()在取Blob的时候会是乱码~
				result.put(this.nc.getPropertyName(c,columnName), rs.getObject(i));
			} else {
				// 不是则按原来逻辑运算
				result.put(this.nc.getPropertyName(c,columnName), rs.getObject(i));
			}
		}

		return result;
	}
	
	/**
	 * 
	 * @ClassName: CaseInsensitiveHashMap   
	 * @Description: 忽略key大小写  
	 * @author: suxj  
	 * @date:2015年8月2日 下午2:01:23
	 */
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

	/**
	 * 
	 * @MethodName: createBean   
	 * @Description: 创建 一个新的对象，并从ResultSet初始化  
	 * @param @param rs
	 * @param @param type
	 * @param @param props
	 * @param @param columnToProperty
	 * @param @return
	 * @param @throws SQLException  
	 * @return T  
	 * @throws
	 */
	private <T> T createBean(ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty) throws SQLException {

		T bean = this.newInstance(type);

		for (int i = 1; i < columnToProperty.length; i++) {
			//Array.fill数组为-1 ，-1则无对应name
			if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
				if(bean instanceof Bean2){
					Bean2  bean2 = (Bean2)bean;
					Object value = rs.getObject(i);
					String key = rs.getMetaData().getColumnLabel(i);
					key = this.nc.getPropertyName(type, key);
					bean2.set(key, value);
				}
				continue;
			}

			//columnToProperty[i]取出对应的在PropertyDescriptor[]中的下标
			PropertyDescriptor prop = props[columnToProperty[i]];
			Class<?> propType = prop.getPropertyType();

			Object value = null;
			if (propType != null) {
				value = this.processColumn(rs, i, propType);

				if (value == null && propType.isPrimitive()) {
					value = primitiveDefaults.get(propType);
				}
			}

			this.callSetter(bean, prop, value);
		}

		return bean;
		
	}

	/**
	 * 
	 * @MethodName: callSetter   
	 * @Description: 根据setter方法设置值  
	 * @param @param target
	 * @param @param prop
	 * @param @param value
	 * @param @throws SQLException  
	 * @return void  
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private void callSetter(Object target, PropertyDescriptor prop, Object value) throws SQLException {

		Method setter = prop.getWriteMethod();

		if (setter == null) return;

		Class<?>[] params = setter.getParameterTypes();
		try {
			//对date特殊处理
			if (value instanceof java.util.Date) {
				final String targetType = params[0].getName();
				if ("java.sql.Date".equals(targetType)) {
					value = new java.sql.Date(((java.util.Date) value).getTime());
				} else if ("java.sql.Time".equals(targetType)) {
					value = new java.sql.Time(((java.util.Date) value).getTime());
				} else if ("java.sql.Timestamp".equals(targetType)) {
					Timestamp tsValue = (Timestamp) value;
					int nanos = tsValue.getNanos();
					value = new java.sql.Timestamp(tsValue.getTime());
					((Timestamp) value).setNanos(nanos);
				}
			} else if (value instanceof String && params[0].isEnum()) {
				value = Enum.valueOf(params[0].asSubclass(Enum.class),(String) value);
			}

			//类型是否兼容
			if (this.isCompatibleType(value, params[0])) {
				setter.invoke(target, new Object[] { value });
			} else {
				throw new SQLException("Cannot set " + prop.getName() + ": incompatible types, cannot convert " + value.getClass().getName() + " to " + params[0].getName());
			}
		} catch (IllegalArgumentException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
		}
		
	}

	/**
	 * 
	 * @MethodName: isCompatibleType   
	 * @Description: 判断类型是否兼容  
	 * @param @param value
	 * @param @param type
	 * @param @return  
	 * @return boolean  
	 * @throws
	 */
	private boolean isCompatibleType(Object value, Class<?> type) {

		//type.isInstance(value) valye是否于type类型兼容
		if (value == null || type.isInstance(value)) return true;
		else if (type.equals(Integer.TYPE) && value instanceof Integer) return true;
		else if (type.equals(Long.TYPE) && value instanceof Long) return true;
		else if (type.equals(Double.TYPE) && value instanceof Double) return true;
		else if (type.equals(Float.TYPE) && value instanceof Float) return true;
		else if (type.equals(Short.TYPE) && value instanceof Short) return true;
		else if (type.equals(Byte.TYPE) && value instanceof Byte) return true;
		else if (type.equals(Character.TYPE) && value instanceof Character) return true;
		else if (type.equals(Boolean.TYPE) && value instanceof Boolean) return true;
		else return false;

	}

	/**
	 * 
	 * @MethodName: newInstance   
	 * @Description: 反射对象  
	 * @param @param c
	 * @param @return
	 * @param @throws SQLException  
	 * @return T  
	 * @throws
	 */
	protected <T> T newInstance(Class<T> c) throws SQLException {
		
		try {
			return c.newInstance();

		} catch (InstantiationException e) {
			throw new BeetlSQLException(BeetlSQLException.OBJECT_INSTANCE_ERROR,e);

		} catch (IllegalAccessException e) {
			throw new BeetlSQLException(BeetlSQLException.OBJECT_INSTANCE_ERROR,e);
		}
		
	}

	/**
	 * 
	 * @MethodName: propertyDescriptors   
	 * @Description: 根据class取得属性描述PropertyDescriptor  
	 * @param @param c
	 * @param @return
	 * @param @throws SQLException  
	 * @return PropertyDescriptor[]  
	 * @throws
	 */
	private PropertyDescriptor[] propertyDescriptors(Class<?> c) throws SQLException {
		
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(c);
		} catch (IntrospectionException e) {
			throw new SQLException("Bean introspection failed: " + e.getMessage());
		}

		return beanInfo.getPropertyDescriptors();
		
	}

	/**
	 * 
	 * @MethodName: mapColumnsToProperties   
	 * @Description: 记录存在name在 PropertyDescriptor中的下标
	 * @param @param rsmd
	 * @param @param props
	 * @param @return
	 * @param @throws SQLException  
	 * @return int[]  
	 * @throws
	 */
	protected int[] mapColumnsToProperties(Class<?> c,ResultSetMetaData rsmd, PropertyDescriptor[] props) throws SQLException {

		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];
		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			String propertyName = columnToPropertyOverrides.get(columnName);
			if (propertyName == null) {
				propertyName = columnName;
			}
			for (int i = 0; i < props.length; i++) {

//				if (propertyName.equalsIgnoreCase(props[i].getName())) {//这里是一个扩展点，用来扩展pojo属性到数据库字段的映射
				if(props[i].getName().equalsIgnoreCase(this.nc.getPropertyName(c,propertyName))) {
					columnToProperty[col] = i;
					break;
				}
			}
		}

		return columnToProperty;
		
	}

	/**
	 * 
	 * @MethodName: processColumn   
	 * @Description: 获取字段值并转换为对应类型
	 * @param @param rs
	 * @param @param index 第几个元素
	 * @param @param propType
	 * @param @return
	 * @param @throws SQLException  
	 * @return Object  
	 * @throws
	 */
	protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {

		//propType.isPrimitive是否为8种基本类型之一
		if (!propType.isPrimitive() && rs.getObject(index) == null) return null;
		if (propType.equals(String.class)) return rs.getString(index);
		else if (propType.equals(Integer.TYPE) || propType.equals(Integer.class)) return Integer.valueOf(rs.getInt(index));
		else if (propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) return Boolean.valueOf(rs.getBoolean(index));
		else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) return Long.valueOf(rs.getLong(index));
		else if (propType.equals(Double.TYPE) || propType.equals(Double.class)) return Double.valueOf(rs.getDouble(index));
		else if (propType.equals(Float.TYPE) || propType.equals(Float.class)) return Float.valueOf(rs.getFloat(index));
		else if (propType.equals(Short.TYPE) || propType.equals(Short.class)) return Short.valueOf(rs.getShort(index));
		else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) return Byte.valueOf(rs.getByte(index));
		
		else if(propType.equals(char[].class)) return rs.getString(index).toCharArray();
		else if(propType.equals(byte[].class)) return rs.getBytes(index);
		
		else if (propType.equals(Timestamp.class)) return rs.getTimestamp(index);
		else if (propType.equals(SQLXML.class)) return rs.getSQLXML(index);
		else return rs.getObject(index);

	}

}
