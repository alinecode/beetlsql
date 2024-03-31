package org.beetl.sql.annotation.builder;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 把日期类型转为long存储到数据库，需要pojo的类型是日期类型，数据库字段是long类型
 * <pre>@{code
 * @Date2Long
 * Timestamp create_ts
 * }</pre>
 * @author xiandafu
 */
public class Date2LongConvert implements  AttributeConvert{
	/**
	 * 在存储到数据库前的转化
	 * @param ctx
	 * @param cls
	 * @param name 属性名
	 * @param pojo  传入的Pojo
	 * @return
	 */
	public Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {
		Object value =  BeanKit.getBeanProperty(pojo, name);
		if(value==null){
			return null;
		}
		Long time = 0L;
		if (value instanceof Timestamp) {
			time = ((Timestamp) value).getTime();
		} else if (value instanceof Date) {
			time = ((Date) value).getTime();
		} else {
			throw new UnsupportedOperationException("@Date2Long 不支持的类型 " + value.getClass() + ",期望是 Date或者Timestamp");
		}

		return time;
	}

	/**
	 * 从数据库取值后的转化
	 * @param ctx
	 * @param cls
	 * @param name
	 * @param rs
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
		Class type = BeanKit.getPropertyDescriptor(cls,name).getPropertyType();
		Long time = rs.getLong(index);
		if(time==null){
			return null;
		}
		if(type==java.util.Date.class){
			return new Date(time);
		}else if(type==java.sql.Date.class){
			return new java.sql.Date(time);
		}
		else if(type==java.sql.Timestamp.class){
			return new Timestamp(time);
		}else{
			throw new UnsupportedOperationException("@Date2Long 不支持的类型 "+type+",期望是 Date或者Timestamp");
		}

	}
}
