package org.beetl.sql.core.mapping;

import java.beans.IntrospectionException;
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
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.EnumKit;
import org.beetl.sql.core.kit.LobKit;
import org.beetl.sql.core.mapping.type.BigDecimalTypeHandler;
import org.beetl.sql.core.mapping.type.DefaultTypeHandler;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;

/**
 * ResultSet处理类，负责转换到Bean或者Map
 * @author: suxj,xiandafu
 */
public class BeanProcessor {

	protected static final int PROPERTY_NOT_FOUND = 0;
	private NameConversion nc = null;
	SQLManager sm ;
	String dbName;
	Map<Class,JavaSqlTypeHandler> handlers = new HashMap<Class,JavaSqlTypeHandler>();
	JavaSqlTypeHandler defaultHandler = new DefaultTypeHandler();
	
	public BeanProcessor(NameConversion nc,SQLManager sm) {
		this.nc = nc;
		this.sm = sm;
		this.dbName = sm.getDbStyle().getName();
		
	}
	private void initHandlers(){
		handlers.put(BigDecimal.class, new BigDecimalTypeHandler());
	}

	

	
	/**
	 * 将ResultSet映射为一个POJO对象 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> T toBean(String sqlId,ResultSet rs, Class<T> type) throws SQLException {

		PropertyDescriptor[] props = this.propertyDescriptors(type);

		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(type,rsmd, props);

		return this.createBean(sqlId,rs, type, props, columnToProperty);
		
	}
	
	/**
	 * 将ResultSet映射为一个POJO对象 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {

		return toBean(null,rs,type);
		
	}


	
	/**
	 * 将ResultSet映射为一个List&lt;POJO&gt;集合 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> toBeanList(String sqlId,ResultSet rs, Class<T> type) throws SQLException {
		
		List<T> results = new ArrayList<T>();

		if (!rs.next()) {
			return results;
		}

		PropertyDescriptor[] props = this.propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(type,rsmd, props);

		do {
			results.add(this.createBean(sqlId,rs, type, props, columnToProperty));
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
	public Map<String, Object> toMap(String sqlId,Class<?> c,ResultSet rs) throws SQLException {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = BeanKit.getMapIns(c);
		if(c==null){
			throw new SQLException("不能映射成Map:"+c);
		}
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
//		String tableName = nc.getTableName(c);
		TypeParameter tp = new TypeParameter(sqlId,dbName,null,rs,rsmd,0);
		for (int i = 1; i <= cols; i++) {
			
			String columnName = rsmd.getColumnLabel(i);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(i);
			}
			int colType = rsmd.getColumnType(i);
			Class  classType = JavaType.jdbcJavaTypes.get(colType);
			JavaSqlTypeHandler handler = handlers.get(classType);
			
			if(handler==null){
				handler = this.defaultHandler;
			}
			tp.setIndex(i);
			tp.setTarget(classType);
			Object value = handler.getValue(tp);
			result.put(this.nc.getPropertyName(c,columnName), value);
			
		}

		return result;
	}
	
	
	public Object toBaseType(String sqlId,Class<?> c,ResultSet rs) throws SQLException {
		TypeParameter tp = new TypeParameter(sqlId,dbName,null,rs,rs.getMetaData(),1);
		JavaSqlTypeHandler handler = handlers.get(c);
		
		if(handler==null){
			handler = this.defaultHandler;
		}
		return handler.getValue(tp);
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
	private <T> T createBean(String sqlId,ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty) throws SQLException {

		T bean = this.newInstance(type);
		ResultSetMetaData meta = rs.getMetaData();
		TypeParameter tp = new TypeParameter(sqlId,this.dbName,type,rs,meta,1);
		
		for (int i = 1; i < columnToProperty.length; i++) {
			//Array.fill数组为-1 ，-1则无对应name
			tp.setIndex(i);
			if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
				String key = rs.getMetaData().getColumnLabel(i);
				if(key.equals("beetl_rn")){
					//sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
					continue;
				}
				
				if(bean instanceof Tail){
					Tail  bean2 = (Tail)bean;
					Object value = noMappingValue(tp);
					key = this.nc.getPropertyName(type, key);
					bean2.set(key, value);
				}else{
					Method m = BeanKit.getTailMethod(type);
					//使用指定方法赋值
					if(m!=null){
						
						Object value = noMappingValue(tp);
						key = this.nc.getPropertyName(type, key);
						try {
							m.invoke(bean, new Object[]{key,value});
						} catch (Exception ex) {
							throw new BeetlSQLException(BeetlSQLException.TAIL_CALL_ERROR,ex);
						} 
					}else{
						// 忽略这个结果集
					}
				}
				continue;
			}

			//columnToProperty[i]取出对应的在PropertyDescriptor[]中的下标
			PropertyDescriptor prop = props[columnToProperty[i]];
			Class<?> propType = prop.getPropertyType();
			JavaSqlTypeHandler handler = this.handlers.get(propType);
			if(handler==null){
				handler = this.defaultHandler;
			}
			Object value = handler.getValue(tp);
			this.callSetter(bean, prop, value,propType);
		}

		return bean;
		
	}

	private Object noMappingValue(TypeParameter tp) throws SQLException{
		Object value =null;
		Class expectedType =JavaType.jdbcJavaTypes.get(tp.getColumnType());
		if(expectedType!=null){
			JavaSqlTypeHandler handler = this.handlers.get(expectedType);
			if(handler==null){
				value = tp.getObject();
			}else{
				value = handler.getValue(tp);
			}
		}else{
			value = tp.getObject();
		}
		return value;
	}

	/**
	 * 根据setter方法设置值
	 * @param target
	 * @param prop
	 * @param value
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private void callSetter(Object target, PropertyDescriptor prop, Object value,Class type) throws SQLException {

		Method setter = prop.getWriteMethod();
		if (setter == null) return;
		if (type.isEnum()) {

			value = EnumKit.getEnumByValue(type, value);
			if(value==null){
				throw new SQLException("Cannot set ENUM " + prop.getName() + ": Convert to NULL for value"+ value);
				
			}
		}
		try {
			setter.invoke(target, new Object[] { value });
		} catch (IllegalArgumentException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
		}
		
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
		
		try {
			return BeanKit.propertyDescriptors(c);
		} catch (IntrospectionException e) {
			throw new SQLException("Bean introspection failed: " + e.getMessage());
		}
		
		
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
		//TODO 性能优化？
		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			
			for (int i = 0; i < props.length; i++) {

				if(props[i].getName().equalsIgnoreCase(this.nc.getPropertyName(c,columnName))) {
					columnToProperty[col] = i;
					break;
				}
			}
		}

		return columnToProperty;
		
	}

	


}
