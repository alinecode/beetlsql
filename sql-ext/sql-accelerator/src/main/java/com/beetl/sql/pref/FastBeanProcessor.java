package com.beetl.sql.pref;

import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.ClassAnnotation;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.ReadTypeParameter;
import org.beetl.sql.core.range.RangeSql;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 暂时没有，未来考虑 把toBean中的反射赋值替换成asm 直接调用
 */
public   class FastBeanProcessor extends BeanProcessor {

	@Override
	public <T> List<T> toBeanList(ExecuteContext ctx, ResultSet rs, Class<T> type) throws SQLException {


		if (!rs.next()) {
			return new ArrayList<T>(0);
		}
		List<T> results = newList();
		PropertyDescriptor[] props = this.propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = this.mapColumnsToProperties(ctx,type, rsmd, props);
		//增加一个属性写辅助类
		BeanPropertyWrite beanPropertyWrite = BeanPropertyWriteFactory.getBeanPropertyWrite(type);
		do {
			results.add(this.createBean(beanPropertyWrite,ctx, rs, type, props, columnToProperty));
		} while (rs.next());

		return results;

	}

	protected <T> T createBean(BeanPropertyWrite beanPropertyWrite,ExecuteContext ctx, ResultSet rs, Class<T> type, PropertyDescriptor[] props,
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

			beanPropertyWrite.setValue(columnToProperty[i],bean,value);

		}
		return bean;
	}
}
