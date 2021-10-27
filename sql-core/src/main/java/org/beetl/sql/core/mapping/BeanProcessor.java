package org.beetl.sql.core.mapping;

import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.entity.View;
import org.beetl.sql.clazz.ClassAnnotation;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.EnumKit;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.mapping.join.JsonConfigMapper;
import org.beetl.sql.core.mapping.type.*;
import org.beetl.sql.core.range.RangeSql;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.Date;

/**
 * ResultSet处理类，负责转换到Bean或者Map，可以通过SQLManagerBuilder设置一个自定义的BeanProcessor
 * 参考 {@link SQLManagerBuilder#setBeanProcessor(BeanProcessor)}
 *
 * 一些复杂的处理类也可以使用此类作为基础，比如{@link JsonConfigMapper}
 * @author: xiandafu,suxj
 */
public class BeanProcessor {

	protected static final int PROPERTY_NOT_FOUND = -1;
	protected static final int PROPERTY_IGNORE = -2;
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
	static UtilDateTypeHandler utilDateTypeHandler = new UtilDateTypeHandler();
	static SqlXMLTypeHandler sqlXMLTypeHandler = new SqlXMLTypeHandler();
	static StringTypeHandler stringTypeHandler = new StringTypeHandler();
	static TimestampTypeHandler timestampTypeHandler = new TimestampTypeHandler();
	static TimeTypeHandler timeTypeHandler = new TimeTypeHandler();
	static CLobJavaSqlTypeHandler clobTypeHandler = new CLobJavaSqlTypeHandler();
	static BlobJavaSqlTypeHandler blobTypeHandler = new BlobJavaSqlTypeHandler();
	static LocalDateTimeTypeHandler localDateTimeHandler = new LocalDateTimeTypeHandler();
	static LocalDateTypeHandler localDateHandler = new LocalDateTypeHandler();

	/*根据类型找到对应的处理类*/
	protected Map<Class, JavaSqlTypeHandler> handlers = new HashMap<Class, JavaSqlTypeHandler>();
	/*如果通过handlers找不到匹配的类型处理类，则使用AcceptType寻找*/
	List<AcceptType> acceptTypeList = new ArrayList<>();

	protected JavaSqlTypeHandler defaultHandler = new DefaultTypeHandler();


	public BeanProcessor() {

		initHandlers();
	}

	private void initHandlers() {
		handlers.put(BigDecimal.class, bigDecimalHandler);
		handlers.put(Boolean.class, booleanDecimalHandler);
		handlers.put(boolean.class, booleanDecimalHandler);
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
		handlers.put(java.util.Date.class, utilDateTypeHandler);
		handlers.put(SQLXML.class, sqlXMLTypeHandler);
		handlers.put(String.class, stringTypeHandler);
		handlers.put(Timestamp.class, timestampTypeHandler);
		handlers.put(Time.class, timeTypeHandler);
		handlers.put(Clob.class, clobTypeHandler);
		handlers.put(Blob.class, blobTypeHandler);
		handlers.put(LocalDateTime.class,localDateTimeHandler);
		handlers.put(LocalDate.class,localDateHandler);

		acceptTypeList.add(new TemporalAcceptType(dateTypeHandler));
		acceptTypeList.add(new EnumAcceptType());
	}

	/**
	 * 把结果集转成对象
	 * @param ctx
	 * @param rs
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> mappingSelect(ExecuteContext ctx,ResultSet rs, Class<T> clazz) throws SQLException {
		List<T> resultList = null;
		//类型判断需要做性能优化，比如每个类提供一个Handler，需要确认此处是否需要优化
		if (Map.class.isAssignableFrom(clazz)) {
			// 如果是Map的子类或者父类，返回List<Map<String,Object>>
			resultList = new ArrayList<T>();
			while (rs.next()) {

				Map map = toMap(ctx, clazz, rs);
				resultList.add((T) map);
			}
			return resultList;

		} else if (BeanKit.isBaseDataType(clazz)) {
			resultList = new ArrayList<T>(1);
			while (rs.next()) {
				Object result = toBaseType(ctx, clazz, rs);
				resultList.add((T) result);
			}
		} else {
			resultList = toBeanList(ctx, rs, clazz);
			return resultList;
		}
		return resultList;

	}

	/**
	 * 将ResultSet映射为一个POJO对象 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> T toBean(ExecuteContext ctx, ResultSet rs, Class<T> type) throws SQLException {

		PropertyDescriptor[] props = this.propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(ctx,type, rsmd, props);
		return this.createBean(ctx, rs, type, props, columnToProperty);

	}

	/**
	 * 将ResultSet映射为一个List&lt;POJO&gt;集合 
	 * @param rs
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> toBeanList(ExecuteContext ctx, ResultSet rs, Class<T> type) throws SQLException {


		if (!rs.next()) {
			return new ArrayList<T>(0);
		}
		List<T> results = new ArrayList<T>();
		PropertyDescriptor[] props = this.propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(ctx,type, rsmd, props);

		do {
			results.add(this.createBean(ctx, rs, type, props, columnToProperty));
		} while (rs.next());

		return results;

	}


	/**
	 * 将rs转化为Map&lt;String ,Object&gt;
	 * @param ctx 上下文
	 * @param c 一个Map子类
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> toMap(ExecuteContext  ctx, Class<?> c, ResultSet rs) throws SQLException {

		@SuppressWarnings("unchecked") Map<String, Object> result = BeanKit.getMapIns(c);
		if (c == null) {
			throw new SQLException("不能映射成Map:" + c);
		}

		SqlId sqlId = ctx.sqlId;
		NameConversion nc = ctx.sqlManager.getNc();
		DBStyle dbStyle = ctx.sqlManager.getDbStyle();
		String dbName = dbStyle.getName();
		int dbType = dbStyle.getDBType();

		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		//		String tableName = nc.getTableName(c);
		ReadTypeParameter tp = new ReadTypeParameter(sqlId, dbName, null, rs, rsmd, 0,ctx);
		for (int i = 1; i <= cols; i++) {
			String columnName = this.getColName(ctx,rsmd,i);

			int colType = rsmd.getColumnType(i);
			if ((dbType == DBType.DB_ORACLE || dbType == DBType.DB_SQLSERVER) && columnName
					.equalsIgnoreCase(RangeSql.PAGE_FLAG)) {
				//sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
				continue;
			}
			Class classType = JavaType.jdbcJavaTypes.get(colType);
			JavaSqlTypeHandler handler = this.getHandler(classType);

			if (handler == null) {
				handler = this.defaultHandler;
			}
			tp.setIndex(i);
			tp.setTarget(classType);
			Object value = handler.getValue(tp);
			result.put(nc.getPropertyName(c, columnName), value);

		}

		return result;
	}


	public Object toBaseType(ExecuteContext  ctx, Class<?> c, ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();

		SqlId sqlId = ctx.sqlId;
		NameConversion nc = ctx.sqlManager.getNc();
		DBStyle dbStyle = ctx.sqlManager.getDbStyle();
		String dbName = dbStyle.getName();
		int dbType = dbStyle.getDBType();

		int count = meta.getColumnCount();
		int index = 0;
		if (count == 1) {
			index = 1;

		} else if (count == 2 && (dbType == DBType.DB_ORACLE || dbType == DBType.DB_SQLSERVER)) {
			//猜测可能有翻页beetl_rn,取出有效列
			String name1 = meta.getColumnName(1);
			String name2 = meta.getColumnName(2);
			index = name2.equalsIgnoreCase(RangeSql.PAGE_FLAG) ? 1 : 2;
		}

		if (index == 0) {
			throw new SQLException("Beetlsql查询期望返回一列，返回类型为" + c + " 但返回了" + count + "列，" + sqlId);
		}

		ReadTypeParameter tp = new ReadTypeParameter(sqlId, dbName, c, rs, meta, index,ctx);
		JavaSqlTypeHandler handler = this.getHandler(c);
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
	protected <T> T createBean(ExecuteContext ctx, ResultSet rs, Class<T> type, PropertyDescriptor[] props,
				int[] columnToProperty) throws SQLException {

		T bean = this.newInstance(type);
		ResultSetMetaData meta = rs.getMetaData();
		Class viewType = ctx.viewClass;

		SqlId sqlId = ctx.sqlId;
		NameConversion nc = ctx.sqlManager.getNc();
		DBStyle dbStyle = ctx.sqlManager.getDbStyle();
		String dbName = dbStyle.getName();
		int dbType = dbStyle.getDBType();

		ReadTypeParameter tp = new ReadTypeParameter(sqlId, dbName, type, rs, meta, 1,ctx);

		ClassAnnotation ca = ClassAnnotation.getClassAnnotation(type);
		Map<String, AttributeConvert> attrMap = null;
		AttributeConvert convert = null;
		if(ca.isContainExtAnnotation()){
			 attrMap =ca.getExtAnnotation().getAttributeConvertMap();

		}
		for (int i = 1; i < columnToProperty.length; i++) {
			//Array.fill数组为-1 ，-1则无对应name
			if(columnToProperty[i] == PROPERTY_IGNORE){
				continue;
			}
			tp.setIndex(i);
			if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
				String key = this.getColName(ctx,meta,i);
				if ((dbType == DBType.DB_ORACLE || dbType == DBType.DB_SQLSERVER) && key
						.equalsIgnoreCase(RangeSql.PAGE_FLAG)) {
					//sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
					continue;
				}
				if (bean instanceof Tail) {
					Tail bean2 = (Tail) bean;
					Object value = noMappingValue(tp);
					key = nc.getPropertyName(type, key);
					bean2.set(key, value);
				}
				continue;
			}
			//columnToProperty[i]取出对应的在PropertyDescriptor[]中的下标
			PropertyDescriptor prop = props[columnToProperty[i]];
			Class<?> propType = prop.getPropertyType();
			Object value = null;
			if(attrMap!=null){
				convert = attrMap.get(prop.getName());
			}
			if(convert!=null){
				value = convert.toAttr(ctx,type,prop.getName(),rs,i);
			}else{
				tp.setTarget(propType);
				JavaSqlTypeHandler handler = this.getHandler(propType);
				if (handler == null) {
					handler = this.defaultHandler;
				}
				value = handler.getValue(tp);
			}
			this.callSetter(bean, prop, value, propType);
		}
		return bean;
	}

	protected Object noMappingValue(ReadTypeParameter tp) throws SQLException {
		Object value = null;
		Class expectedType = JavaType.jdbcJavaTypes.get(tp.getColumnType());
		if (expectedType != null) {
			JavaSqlTypeHandler handler = this.getHandler(expectedType);
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
	 * @param target 目标Bean
	 * @param prop
	 * @param value 值
	 * @param type   值类型
	 * @throws SQLException
	 */
	public void callSetter(Object target, PropertyDescriptor prop, Object value, Class<?> type) throws SQLException {

		Method setter = BeanKit.getWriteMethod(prop, target.getClass());
		if (setter == null) {
			return;
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


	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * 记录存在name在 PropertyDescriptor中的下标
	 * @param c
	 * @param rsmd
	 * @param props
	 * @return
	 * @throws SQLException
	 */
	protected int[] mapColumnsToProperties(ExecuteContext ctx,Class<?> c, ResultSetMetaData rsmd, PropertyDescriptor[] props)
			throws SQLException {
		NameConversion nc = ctx.sqlManager.getNc();
		Class viewType = ctx.viewClass;
		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];
		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
		//TODO 性能优化？
		for (int col = 1; col <= cols; col++) {
			String columnName = getColName(ctx,rsmd,col);
			String expectedProperty = nc.getPropertyName(c, columnName);
			for (int i = 0; i < props.length; i++) {

				if (props[i].getName().equalsIgnoreCase(expectedProperty)) {
					if(viewType==null){
						//大部分情况下
						columnToProperty[col] = i;
					}else{
						//检测此属性是否属于此列
						View viewAnnotation = BeanKit.getAnnotation(c,props[i].getName(),props[i].getReadMethod(), View.class);
						if(viewAnnotation==null){
							continue;
						}
						if(!BeanKit.containViewType(viewAnnotation.value(),viewType)){
							continue;
						}
						columnToProperty[col] = PROPERTY_IGNORE;;
					}
					break;
				}
			}
		}

		return columnToProperty;

	}

	protected  String getColName(ExecuteContext ctx,ResultSetMetaData rsmd,int col) throws SQLException{
		String columnName = rsmd.getColumnLabel(col);
		if (null == columnName || 0 == columnName.length()) {
			columnName = rsmd.getColumnName(col);
		}
		return columnName;
	}

	/**
	 * 设置PreparedStatement的参数，beetlsql可以重载此类实现个性化设置方案，也可可以根据根据sqlId做一定个性化设置
	 * @param ctx
	 * @param ps
	 * @param objs
	 * @throws SQLException
	 */
	public void setPreparedStatementPara(ExecuteContext ctx, PreparedStatement ps, List<SQLParameter> objs)
			throws SQLException {
		SqlId sqlId = ctx.sqlId;
		NameConversion nc = ctx.sqlManager.getNc();
		DBStyle dbStyle = ctx.sqlManager.getDbStyle();
		String dbName = dbStyle.getName();
		int dbType = dbStyle.getDBType();
		int i = 0;
		SQLParameter para = null;
		WriteTypeParameter writeTypeParameter = new WriteTypeParameter(ctx.sqlId,dbName,dbType,ctx.target,ps,0,ctx);
		try {
			for (; i < objs.size(); i++) {
				para = objs.get(i);
				Object o = para.value;
				int jdbcType = para.getJdbcType();
				if (o == null) {
					if (jdbcType != 0) {
						ps.setNull(i + 1, jdbcType);
					} else {
						ps.setObject(i + 1, o);
					}
					continue;
				}

				writeTypeParameter.setIndex(i+1);
				Class c = o.getClass();
				JavaSqlTypeHandler handler = this.getHandler(c);
				if(handler==null){
					handler = this.defaultHandler;
				}
				if(jdbcType!=0){
					ps.setObject(i + 1, o, jdbcType);
				}else{
					//最常见的情况
					handler.setParameter(writeTypeParameter,o);
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

	public void addHandler(Class target,JavaSqlTypeHandler handler){
		handlers.put(target,handler);
	}

	public void addAcceptType(AcceptType acceptType){
		//总是加到最前
		this.acceptTypeList.add(0,acceptType);
	}

	/**
	 * 得到类型处理器
	 * @param target
	 * @return 如果target为null，则返回默认处理器
	 */
	public JavaSqlTypeHandler getHandler(Class target){
		if(target==null){
			return this.defaultHandler;
		}
		JavaSqlTypeHandler handler = handlers.get(target);
		if(handler==null&&!this.acceptTypeList.isEmpty()){
			for(AcceptType acceptType:acceptTypeList){
				handler = acceptType.isAccept(target);
				if(handler!=null){
					break;
				}
			}
		}
		return handler;
	}

	public static interface AcceptType{
		public JavaSqlTypeHandler isAccept(Class cls);
	}

	public static class EnumAcceptType implements AcceptType{
		EnumTypeHandler enumTypeHandler = new EnumTypeHandler();
		@Override
		public JavaSqlTypeHandler isAccept(Class cls) {
			if(Enum.class.isAssignableFrom(cls)){
				return enumTypeHandler;
			}else{
				return null;
			}
		}
	}

	/**
	 * 如果jdbc取值支持Temporal，
	 * @see "https://gitee.com/xiandafu/beetlsql/issues/I4AKMM"
	 */
	public static class TemporalAcceptType implements AcceptType{

		TemporalTypeHandler temporalHandler ;
		DateTypeHandler dateTypeHandler;
		public TemporalAcceptType(DateTypeHandler dateTypeHandler){
			this.dateTypeHandler = dateTypeHandler;
			temporalHandler = new TemporalTypeHandler();
		}

		@Override
		public JavaSqlTypeHandler isAccept(Class cls) {
			if(Temporal.class.isAssignableFrom(cls)){
				return temporalHandler;
			}if(Date.class.isAssignableFrom(cls)){
				return dateTypeHandler;
			}else{
				return null;
			}
		}
	}

	/**
	 * 处理枚举类
	 */
	public static class EnumTypeHandler extends   JavaSqlTypeHandler{
		@Override
		public Object getValue(ReadTypeParameter typePara) throws SQLException {
			Object obj = typePara.getObject();
			if(obj==null){
				return null;
			}
			if(typePara.target==null){
				return obj;
			}
			//转化成期望的枚举值
			Enum enumValue =obj2enum(typePara,obj);
			if (enumValue == null) {
				throw new SQLException("Cannot set ENUM " + typePara.target + ": Convert to NULL for value " + obj);
			}
			return enumValue;
		}


		public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
			Object value = enum2Obj(writeTypeParameter,obj);
			if(value==null){
				writeTypeParameter.getPs().setObject(writeTypeParameter.getIndex(),null);
			}
			Class target  = value.getClass();
			JavaSqlTypeHandler handler = writeTypeParameter.getExecuteContext().sqlManager.getDefaultBeanProcessors().getHandler(target);
			handler.setParameter(writeTypeParameter,value);
		}
		protected  Enum obj2enum(ReadTypeParameter typePara,Object obj){
			SQLManagerExtend sqlManagerExtend = typePara.getExecuteContext().sqlManager.getSqlManagerExtend();
			Enum enumValue =sqlManagerExtend.getEnumExtend().getEnumByValue(typePara.target,obj);
			return enumValue;
		}
		protected  Object enum2Obj(WriteTypeParameter writeTypeParameter,Object obj){
			SQLManagerExtend sqlManagerExtend = writeTypeParameter.getExecuteContext().sqlManager.getSqlManagerExtend();
			Object value = sqlManagerExtend.getEnumExtend().getValueByEnum(obj);
			return value;
		}
	}

	public static class InheritedAcceptType implements AcceptType{
		Class parent;
		JavaSqlTypeHandler handler;
		public InheritedAcceptType(Class parent,JavaSqlTypeHandler handler){
			this.parent = parent;
			this.handler = handler;
		}
		@Override
		public JavaSqlTypeHandler isAccept(Class cls) {
			if(parent.isAssignableFrom(cls)){
				return handler;
			}else{
				return null;
			}
		}
	}
}
