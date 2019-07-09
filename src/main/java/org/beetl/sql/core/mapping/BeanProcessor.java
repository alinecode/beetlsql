package org.beetl.sql.core.mapping;

import org.beetl.sql.core.*;
import org.beetl.sql.core.annotatoin.builder.AttributeBuilderHolder;
import org.beetl.sql.core.annotatoin.builder.AttributeSelectBuilder;
import org.beetl.sql.core.db.ClassAnnotation;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.EnumKit;
import org.beetl.sql.core.mapping.type.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * ResultSet处理类，负责转换到Bean或者Map
 * @author: suxj, xiandafu
 */
public class BeanProcessor {

	protected static final int PROPERTY_NOT_FOUND = -1;
	static BigDecimalTypeHandler bigDecimalHandler = new BigDecimalTypeHandler();
	static BooleanTypeHandler booleanDecimalHandler = new BooleanTypeHandler();
	static ByteArrayTypeHandler byteArrayTypeHandler = new ByteArrayTypeHandler();
	static ByteTypeHandler byteTypeHandler = new ByteTypeHandler();
	static CharArrayTypeHandler charArrayTypeHandler = new CharArrayTypeHandler();
	static DateTypeHandler dateTypeHandler = new DateTypeHandler();
	static DoubleTypeHandler doubleTypeHandler = new DoubleTypeHandler();
	static FloatTypeHandler floatTypeHandler = new FloatTypeHandler();
	static IntegerTypeHandler integerTypeHandler = new IntegerTypeHandler();
	static LongTypeHandler longTypeHandler = new LongTypeHandler();
	static ShortTypeHandler shortTypeHandler = new ShortTypeHandler();
	static SqlDateTypeHandler sqlDateTypeHandler = new SqlDateTypeHandler();
	static SqlXMLTypeHandler sqlXMLTypeHandler = new SqlXMLTypeHandler();
	static StringTypeHandler stringTypeHandler = new StringTypeHandler();
	static TimestampTypeHandler timestampTypeHandler = new TimestampTypeHandler();
	static TimeTypeHandler timeTypeHandler = new TimeTypeHandler();
	static CLobJavaSqlTypeHandler clobTypeHandler = new CLobJavaSqlTypeHandler();
	static BlobJavaSqlTypeHandler blobTypeHandler = new BlobJavaSqlTypeHandler();
	protected NameConversion nc = null;
	protected SQLManager sm;
	protected String dbName;
	protected int dbType;
	protected Map<Class, JavaSqlTypeHandler> handlers = new HashMap<Class, JavaSqlTypeHandler>();
	protected JavaSqlTypeHandler defaultHandler = new DefaultTypeHandler();


	public BeanProcessor(SQLManager sm) {
		this.nc = sm.getNc();
		this.sm = sm;
		this.dbName = sm.getDbStyle().getName();
		dbType = sm.getDbStyle().getDBType();
		initHandlers();
	}

	private void initHandlers() {
		handlers.put(BigDecimal.class, bigDecimalHandler);
		handlers.put(Boolean.class, booleanDecimalHandler);
		handlers.put(byte[].class, byteArrayTypeHandler);
		handlers.put(byte.class, byteTypeHandler);
		handlers.put(Byte.class, byteTypeHandler);
		handlers.put(char[].class, charArrayTypeHandler);
		handlers.put(java.util.Date.class, dateTypeHandler);
		handlers.put(Double.class, doubleTypeHandler);
		handlers.put(double.class, doubleTypeHandler);
		handlers.put(Float.class, floatTypeHandler);
		handlers.put(float.class, floatTypeHandler);
		handlers.put(Integer.class, integerTypeHandler);
		handlers.put(int.class, integerTypeHandler);
		handlers.put(Long.class, longTypeHandler);
		handlers.put(long.class, longTypeHandler);
		handlers.put(Short.class, shortTypeHandler);
		handlers.put(short.class, shortTypeHandler);
		handlers.put(java.sql.Date.class, sqlDateTypeHandler);
		handlers.put(SQLXML.class, sqlXMLTypeHandler);
		handlers.put(String.class, stringTypeHandler);
		handlers.put(Timestamp.class, timestampTypeHandler);
		handlers.put(Time.class, timeTypeHandler);
		handlers.put(Clob.class, clobTypeHandler);
		handlers.put(Blob.class, blobTypeHandler);

	}


	/**
	 * 将ResultSet映射为一个POJO对象 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> T toBean(String sqlId, ResultSet rs, Class<T> type) throws SQLException {

		PropertyDescriptor[] props = this.propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(type, rsmd, props);
		return this.createBean(sqlId, rs, type, props, columnToProperty);

	}

	/**
	 * 将ResultSet映射为一个POJO对象 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
		return toBean(null, rs, type);
	}

	public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
		return this.toBeanList(null, rs, type);
	}

	/**
	 * 将ResultSet映射为一个List&lt;POJO&gt;集合 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> toBeanList(String sqlId, ResultSet rs, Class<T> type) throws SQLException {


		if (!rs.next()) {
			return new ArrayList<T>(0);
		}
		List<T> results = new ArrayList<T>();
		PropertyDescriptor[] props = this.propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(type, rsmd, props);

		do {
			results.add(this.createBean(sqlId, rs, type, props, columnToProperty));
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
	public Map<String, Object> toMap(String sqlId, Class<?> c, ResultSet rs) throws SQLException {

		@SuppressWarnings("unchecked") Map<String, Object> result = BeanKit.getMapIns(c);
		if (c == null) {
			throw new SQLException("不能映射成Map:" + c);
		}

		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		//		String tableName = nc.getTableName(c);
		TypeParameter tp = new TypeParameter(sqlId, dbName, null, rs, rsmd, 0);
		for (int i = 1; i <= cols; i++) {

			String columnName = rsmd.getColumnLabel(i);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(i);
			}
			int colType = rsmd.getColumnType(i);
			if ((dbType == DBStyle.DB_ORACLE || dbType == DBStyle.DB_SQLSERVER) && columnName
					.equalsIgnoreCase("beetl_rn")) {
				//sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
				continue;
			}
			Class classType = JavaType.jdbcJavaTypes.get(colType);
			JavaSqlTypeHandler handler = handlers.get(classType);

			if (handler == null) {
				handler = this.defaultHandler;
			}
			tp.setIndex(i);
			tp.setTarget(classType);
			Object value = handler.getValue(tp);
			result.put(this.nc.getPropertyName(c, columnName), value);

		}

		return result;
	}


	public Object toBaseType(String sqlId, Class<?> c, ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();

		int count = meta.getColumnCount();
		int index = 0;
		if (count == 1) {
			index = 1;

		} else if (count == 2 && (dbType == DBStyle.DB_ORACLE || dbType == DBStyle.DB_SQLSERVER)) {
			//猜测可能有翻页beetl_rn,取出有效列
			String name1 = meta.getColumnName(1);
			String name2 = meta.getColumnName(2);
			index = name2.equalsIgnoreCase("beetl_rn") ? 1 : 2;

		}

		if (index == 0) {
			throw new SQLException("Beetlsql查询期望返回一列，返回类型为" + c + " 但返回了" + count + "列，" + sqlId);
		}

		TypeParameter tp = new TypeParameter(sqlId, dbName, c, rs, meta, index);
		JavaSqlTypeHandler handler = handlers.get(c);
		if (handler == null) {
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
	protected <T> T createBean(String sqlId, ResultSet rs, Class<T> type, PropertyDescriptor[] props,
			int[] columnToProperty) throws SQLException {

		T bean = this.newInstance(type);
		ResultSetMetaData meta = rs.getMetaData();
		TypeParameter tp = new TypeParameter(sqlId, this.dbName, type, rs, meta, 1);

		for (int i = 1; i < columnToProperty.length; i++) {
			//Array.fill数组为-1 ，-1则无对应name
			tp.setIndex(i);
			if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
				String key = rs.getMetaData().getColumnLabel(i);
				if ((dbType == DBStyle.DB_ORACLE || dbType == DBStyle.DB_SQLSERVER) && key
						.equalsIgnoreCase("beetl_rn")) {
					//sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
					continue;
				}

				if (bean instanceof Tail) {
					Tail bean2 = (Tail) bean;
					Object value = noMappingValue(tp);
					key = this.nc.getPropertyName(type, key);
					bean2.set(key, value);
				} else {
					Method m = BeanKit.getTailMethod(type);
					//使用指定方法赋值
					if (m != null) {

						Object value = noMappingValue(tp);
						key = this.nc.getPropertyName(type, key);
						try {
							m.invoke(bean, key, value);
						} catch (Exception ex) {
							throw new BeetlSQLException(BeetlSQLException.TAIL_CALL_ERROR, ex);
						}
					} else {
						// 忽略这个结果集
					}
				}
				continue;
			}

			//columnToProperty[i]取出对应的在PropertyDescriptor[]中的下标
			PropertyDescriptor prop = props[columnToProperty[i]];
			Class<?> propType = prop.getPropertyType();
			tp.setTarget(propType);
			ClassAnnotation ca = ClassAnnotation.getClassAnnotation(type);
			Object value = null;
			if (!ca.getColHandlers().isEmpty()) {
				AttributeBuilderHolder holder = (AttributeBuilderHolder) ca.getColHandlers().get(prop.getName());
				if (holder != null && holder.supportSelectMapping()) {
					value = ((AttributeSelectBuilder) holder.getInstance())
							.toObject(this.sm, holder.getBeanAnnotaton(), sqlId, tp, prop);
					this.callSetter(bean, prop, value, propType);
					continue;
				}

			}
			JavaSqlTypeHandler handler = this.handlers.get(propType);
			if (handler == null) {
				handler = this.defaultHandler;
			}
			value = handler.getValue(tp);


			this.callSetter(bean, prop, value, propType);
		}

		return bean;

	}

	protected Object noMappingValue(TypeParameter tp) throws SQLException {
		Object value = null;
		Class expectedType = JavaType.jdbcJavaTypes.get(tp.getColumnType());
		if (expectedType != null) {
			JavaSqlTypeHandler handler = this.handlers.get(expectedType);
			if (handler == null) {
				value = tp.getObject();
			} else {
				value = handler.getValue(tp);
			}
		} else {
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
	public void callSetter(Object target, PropertyDescriptor prop, Object value, Class<?> type) throws SQLException {

		Method setter = BeanKit.getWriteMethod(prop, target.getClass());
		if (setter == null)
			return;
		if (type.isEnum()) {
			if (value == null) {
				return;
			}
			Object numValue = EnumKit.getEnumByValue(type, value);
			if (numValue == null) {
				throw new SQLException("Cannot set ENUM " + prop.getName() + ": Convert to NULL for value " + value);
			} else {
				value = numValue;
			}
		}
		try {
			setter.invoke(target, value);
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

		return BeanKit.newInstance(c);

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
	protected int[] mapColumnsToProperties(Class<?> c, ResultSetMetaData rsmd, PropertyDescriptor[] props)
			throws SQLException {

		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];
		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
		//TODO 性能优化？
		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			String expectedProperty = this.nc.getPropertyName(c, columnName);
			for (int i = 0; i < props.length; i++) {

				if (props[i].getName().equalsIgnoreCase(expectedProperty)) {
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
	public void setPreparedStatementPara(String sqlId, PreparedStatement ps, List<SQLParameter> objs)
			throws SQLException {
		int i = 0;
		SQLParameter para = null;
		try {
			for (; i < objs.size(); i++) {
				para = objs.get(i);
				Object o = para.value;
				int jdbcType = para.getJdbcType();
				if (o == null) {
					if (jdbcType != 0) {
						ps.setObject(i + 1, o, jdbcType);
					} else {
						ps.setObject(i + 1, o);
					}

					continue;
				}
				Class c = o.getClass();
				// 兼容性修改：oralce 驱动 不识别util.Date
				if (dbType == DBStyle.DB_ORACLE || dbType == DBStyle.DB_POSTGRES || dbType == DBStyle.DB_DB2
						|| dbType == DBStyle.DB_SQLSERVER) {
					if (c == java.util.Date.class) {
						o = new Timestamp(((java.util.Date) o).getTime());
					}
				}

				if (Enum.class.isAssignableFrom(c)) {
					o = EnumKit.getValueByEnum(o);
				}

				//clob or text
				if (c == char[].class) {
					o = new String((char[]) o);
				}


				if (jdbcType == 0) {
					ps.setObject(i + 1, o);
				} else {
					//通常一些特殊的处理
					ps.setObject(i + 1, o, jdbcType);
				}


			}
		} catch (SQLException ex) {
			throw new SQLException("处理第" + (i + 1) + "个参数错误:" + ex.getMessage(), ex);
		}

	}

	public JavaSqlTypeHandler getDefaultHandler() {
		return defaultHandler;
	}

	public void setDefaultHandler(JavaSqlTypeHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public Map<Class, JavaSqlTypeHandler> getHandlers() {
		return handlers;
	}
}
