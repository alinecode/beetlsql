package org.beetl.sql.core.mapping;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.kit.EnumKit;
import org.beetl.sql.core.kit.LobKit;

/**
 * Pojo处理器，负责转换
 * @author: suxj
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

	SQLManager sm ;
	String dbName;
	protected BeanProcessor() {
		this(new HashMap<String, String>());//为{} 非null
	}
	
	public BeanProcessor(NameConversion nc,SQLManager sm) {
		this();
		this.nc = nc;
		this.sm = sm;
		this.dbName = sm.getDbStyle().getName();
	}

	protected BeanProcessor(Map<String, String> columnToPropertyOverrides) {
		super();
		if (columnToPropertyOverrides == null) {
			throw new IllegalArgumentException("columnToPropertyOverrides map cannot be null");
		}
		this.columnToPropertyOverrides = columnToPropertyOverrides;
	}

	
	/**
	 * 将ResultSet映射为一个POJO对象 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {

		PropertyDescriptor[] props = this.propertyDescriptors(type);

		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(type,rsmd, props);

		return this.createBean(rs, type, props, columnToProperty);
		
	}


	
	/**
	 * 将ResultSet映射为一个List&lt;POJO&gt;集合 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
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
	 * 将rs转化为Map&lt;String ,Object&gt;
	 * @param c
	 * @param rs
	 * @return
	 * @throws SQLException
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
	 * 忽略key大小写  
	 * @author Administrator
	 *
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
	 * 创建 一个新的对象，并从ResultSet初始化
	 * @param rs
	 * @param type
	 * @param props
	 * @param columnToProperty
	 * @return
	 * @throws SQLException
	 */
	private <T> T createBean(ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty) throws SQLException {

		T bean = this.newInstance(type);

		for (int i = 1; i < columnToProperty.length; i++) {
			//Array.fill数组为-1 ，-1则无对应name
			if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
				if(bean instanceof Tail){
					Tail  bean2 = (Tail)bean;
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
	 * 根据setter方法设置值
	 * @param target
	 * @param prop
	 * @param value
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private void callSetter(Object target, PropertyDescriptor prop, Object value) throws SQLException {

		Method setter = prop.getWriteMethod();

		if (setter == null) return;

		Class<?>[] params = setter.getParameterTypes();
		try {
			//一些特殊处理，对date特殊处理
			if (value instanceof java.util.Date) {
				final Class targetType = params[0];
				if (java.sql.Date.class==targetType) {
					value = new java.sql.Date(((java.util.Date) value).getTime());
				} else if (java.sql.Time.class==targetType) {
					value = new java.sql.Time(((java.util.Date) value).getTime());
				} else if (java.sql.Timestamp.class==targetType) {
					Timestamp tsValue = (Timestamp) value;
					int nanos = tsValue.getNanos();
					value = new java.sql.Timestamp(tsValue.getTime());
					((Timestamp) value).setNanos(nanos);
				}
			} else if (params[0].isEnum()) {

				value = EnumKit.getEnumByValue(params[0], value);
				if(value==null){
					throw new SQLException("Cannot set ENUM " + prop.getName() + ": Convert to NULL for value"+ value);
					
				}

			}
			//@todo BigDecimal double 互相转化

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
	 * 判断类型是否兼容
	 * @param value
	 * @param type
	 * @return
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
	 * 反射对象 
	 * @param c
	 * @return
	 * @throws SQLException
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

	/**根据class取得属性描述PropertyDescriptor  
	 * 
	 * @param c
	 * @return
	 * @throws SQLException
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
	 * 记录存在name在 PropertyDescriptor中的下标
	 * @param c
	 * @param rsmd
	 * @param props
	 * @return
	 * @throws SQLException
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
	 * 获取字段值并转换为对应类型
	 * @param rs
	 * @param index
	 * @param propType
	 * @return
	 * @throws SQLException
	 */
	protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		//propType.isPrimitive是否为8种基本类型之一
		if (!propType.isPrimitive() && rs.getObject(index) == null) return null;
		if (propType==String.class){
			if(dbName.equals("oracle")){
				int type = meta.getColumnType(index);
				String name = meta.getColumnName(index);
				switch(type){
				case   java.sql.Types.CLOB:{
					Reader r =	rs.getClob(index).getCharacterStream();
					return LobKit.getString(r);
					}
				case Types.NCLOB:{
					Reader r =	rs.getNClob(index).getCharacterStream();
					return LobKit.getString(r);
				}
				
			
				default:
					//不支持Long 类型（longvarchar)
					return rs.getString(index);
				
				}
			}else{
				return rs.getString(index);
			}
			
		}
		else if (propType.equals(Integer.TYPE) || propType.equals(Integer.class)) return Integer.valueOf(rs.getInt(index));
		else if (propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) return Boolean.valueOf(rs.getBoolean(index));
		else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) return Long.valueOf(rs.getLong(index));
		else if (propType.equals(Double.TYPE) || propType.equals(Double.class)) return Double.valueOf(rs.getDouble(index));
		else if (propType.equals(Float.TYPE) || propType.equals(Float.class)) return Float.valueOf(rs.getFloat(index));
		else if (propType.equals(Short.TYPE) || propType.equals(Short.class)) return Short.valueOf(rs.getShort(index));
		else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) return Byte.valueOf(rs.getByte(index));
		else if(propType.equals(BigDecimal.class)) return rs.getBigDecimal(index);
		else if(propType.equals(char[].class)){
			
			if(dbName.equals("oracle")){
				int type = meta.getColumnType(index);
				switch(type){
				case   java.sql.Types.CLOB:{
					Reader r =	rs.getClob(index).getCharacterStream();
					return LobKit.getString(r).toCharArray();
					}
				case Types.NCLOB:{
				Reader r =	rs.getNClob(index).getCharacterStream();
				return LobKit.getString(r).toCharArray();
				}default:
					return rs.getString(index).toCharArray();
				
				}
			}else{
				return rs.getString(index).toCharArray();
			}
			
		}
		else if(propType.equals(byte[].class)) return rs.getBytes(index);
		
		else if (propType.equals(Timestamp.class)) return rs.getTimestamp(index);
		else if (propType.equals(SQLXML.class)) return rs.getSQLXML(index);
		else return rs.getObject(index);

	}

}
