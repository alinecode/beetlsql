package org.beetl.sql.core.mapping;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
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
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.EnumKit;
import org.beetl.sql.core.mapping.type.BigDecimalTypeHandler;
import org.beetl.sql.core.mapping.type.BooleanTypeHandler;
import org.beetl.sql.core.mapping.type.ByteArrayTypeHandler;
import org.beetl.sql.core.mapping.type.ByteTypeHandler;
import org.beetl.sql.core.mapping.type.CharArrayTypeHandler;
import org.beetl.sql.core.mapping.type.DateTypeHandler;
import org.beetl.sql.core.mapping.type.DefaultTypeHandler;
import org.beetl.sql.core.mapping.type.DoubleTypeHandler;
import org.beetl.sql.core.mapping.type.FloatTypeHandler;
import org.beetl.sql.core.mapping.type.IntegerTypeHandler;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.LongTypeHandler;
import org.beetl.sql.core.mapping.type.ShortTypeHandler;
import org.beetl.sql.core.mapping.type.SqlDateTypeHandler;
import org.beetl.sql.core.mapping.type.SqlXMLTypeHandler;
import org.beetl.sql.core.mapping.type.StringTypeHandler;
import org.beetl.sql.core.mapping.type.TimeTypeHandler;
import org.beetl.sql.core.mapping.type.TimestampTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;

/**
 * ResultSet处理类，负责转换到Bean或者Map
 * @author: suxj,xiandafu
 */
public class BeanProcessor {

	protected static final int PROPERTY_NOT_FOUND = -1;
	protected NameConversion nc = null;
	protected SQLManager sm ;
	protected String dbName;
	protected int dbType ;
	protected Map<Class,JavaSqlTypeHandler> handlers = new HashMap<Class,JavaSqlTypeHandler>();
	protected JavaSqlTypeHandler defaultHandler = new DefaultTypeHandler();
	static BigDecimalTypeHandler bigDecimalHandler = new BigDecimalTypeHandler();
	static BooleanTypeHandler booleanDecimalHandler = new BooleanTypeHandler();
	static ByteArrayTypeHandler byteArrayTypeHandler = new ByteArrayTypeHandler();
	static ByteTypeHandler byteTypeHandler = new ByteTypeHandler();
	static CharArrayTypeHandler charArrayTypeHandler = new CharArrayTypeHandler();
	static DateTypeHandler dateTypeHandler = new DateTypeHandler();
	static  DoubleTypeHandler doubleTypeHandler = new DoubleTypeHandler();
	static FloatTypeHandler floatTypeHandler = new FloatTypeHandler();
	static IntegerTypeHandler integerTypeHandler = new IntegerTypeHandler();
	static LongTypeHandler longTypeHandler = new LongTypeHandler();
	static ShortTypeHandler shortTypeHandler = new ShortTypeHandler();
	static SqlDateTypeHandler sqlDateTypeHandler = new SqlDateTypeHandler();
	static SqlXMLTypeHandler sqlXMLTypeHandler = new SqlXMLTypeHandler();
	static StringTypeHandler stringTypeHandler = new StringTypeHandler();
	static TimestampTypeHandler timestampTypeHandler = new TimestampTypeHandler();
	static TimeTypeHandler timeTypeHandler = new TimeTypeHandler();
	
	
	public BeanProcessor(SQLManager sm) {
		this.nc = sm.getNc();
		this.sm = sm;
		this.dbName = sm.getDbStyle().getName();
		dbType = sm.getDbStyle().getDBType();
		initHandlers();
	}
	private void initHandlers(){
		handlers.put(BigDecimal.class,bigDecimalHandler);
		handlers.put(Boolean.class,booleanDecimalHandler);
		handlers.put(byte[].class,byteArrayTypeHandler);
		handlers.put(byte.class,byteTypeHandler);
		handlers.put(Byte.class,byteTypeHandler);
		handlers.put(char[].class,charArrayTypeHandler);
		handlers.put(java.util.Date.class,dateTypeHandler);
		handlers.put(Double.class,doubleTypeHandler);
		handlers.put(double.class,doubleTypeHandler);
		handlers.put(Float.class,floatTypeHandler);
		handlers.put(float.class,floatTypeHandler);
		handlers.put(Integer.class,integerTypeHandler);
		handlers.put(int.class,integerTypeHandler);
		handlers.put(Long.class,longTypeHandler);
		handlers.put(long.class,longTypeHandler);
		handlers.put(Short.class,shortTypeHandler);
		handlers.put(short.class,shortTypeHandler);
		handlers.put(java.sql.Date.class,sqlDateTypeHandler);
		handlers.put(SQLXML.class,sqlXMLTypeHandler);
		handlers.put(String.class,stringTypeHandler);
		handlers.put(Timestamp.class,timestampTypeHandler);
		handlers.put(Time.class,timeTypeHandler);
		
		
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
		
		

		if (!rs.next()) {
			return new ArrayList<T>(0);
		}
		List<T> results = new ArrayList<T>();
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
		TypeParameter tp = new TypeParameter(sqlId,dbName,c,rs,rs.getMetaData(),1);
		int count = tp.getMeta().getColumnCount();
		if(count!=1){
			throw new SQLException("Beetlsql查询期望返回一列，返回类型为"+c+" 但返回了"+count+"列，"+sqlId);
		}
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
	protected <T> T createBean(String sqlId,ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty) throws SQLException {

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
			tp.setTarget(propType);
			JavaSqlTypeHandler handler = this.handlers.get(propType);
			if(handler==null){
				handler = this.defaultHandler;
			}
			Object value = handler.getValue(tp);
			this.callSetter(bean, prop, value,propType);
		}

		return bean;
		
	}

	protected Object noMappingValue(TypeParameter tp) throws SQLException{
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
	protected void callSetter(Object target, PropertyDescriptor prop, Object value,Class<?> type) throws SQLException {

		Method setter = BeanKit.getWriteMethod(prop, target.getClass());
		if (setter == null) return;
		if (type.isEnum()) {
			if(value==null){
				return ;
			}
			Object numValue = EnumKit.getEnumByValue(type, value);
			if(numValue==null){
				throw new SQLException("Cannot set ENUM " + prop.getName() + ": Convert to NULL for value "+ value);
			}else{
				value = numValue;
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
		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
		//TODO 性能优化？
		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			String expectedProperty = this.nc.getPropertyName(c,columnName);
			for (int i = 0; i < props.length; i++) {

				if(props[i].getName().equalsIgnoreCase(expectedProperty)) {
					columnToProperty[col] = i;
					break;
				}
			}
		}

		return columnToProperty;
		
	}
	/**
	 * 设置PreparedStatement的参数，beetlsql可以重载此类实现个性化设置方案，也可可以根据根据sqlId做一定个性化设置
	 * @param sqlId
	 * @param ps
	 * @param objs
	 * @throws SQLException
	 */
	public  void setPreparedStatementPara(String sqlId,PreparedStatement ps,List<SQLParameter> objs) throws SQLException {
		for (int i = 0; i < objs.size(); i++) {
			SQLParameter para = objs.get(i);
			Object o = para.value;
			if(o==null){
				ps.setObject(i + 1, o);
				continue ;
			}
			Class c = o.getClass();
			// 兼容性修改：oralce 驱动 不识别util.Date
			if(dbType==DBStyle.DB_ORACLE||dbType==DBStyle.DB_POSTGRES||dbType==DBStyle.DB_DB2||dbType==DBStyle.DB_SQLSERVER){
				if(c== java.util.Date.class){
					o = new Timestamp(((java.util.Date) o).getTime());
				}
			}
			
			if(Enum.class.isAssignableFrom(c)){
				o = EnumKit.getValueByEnum(o);
			}
			
			//clob or text
			if(c==char[].class){
				o = new String((char[])o);
			}
			
			
			int jdbcType = para.getJdbcType();
			if(jdbcType==0){
				ps.setObject(i + 1, o);
			}else{
				//通常一些特殊的处理
				throw new UnsupportedOperationException(jdbcType+",默认处理器并未处理此jdbc类型");
			}
			
			
			
		}
	}

	


}
