package org.beetl.sql.annotation.builder;

import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 一个持久化前更新字段，使用当前日期格式
 * @see UpdateTime
 * @author xiandafu,whcrow(https://gitee.com/whcrow)
 */
public class UpdateTimeConvert implements AttributeConvert {
    @Override
    public  Object toDb(ExecuteContext ctx,  Class cls,String name, Object dbValue){
		UpdateTime fillDateTime = BeanKit.getAnnotation(cls, name, UpdateTime.class);
		FillStrategy fillStrategy = fillDateTime.value();
		SQLType sqlType = ctx.sqlSource.getSqlType();
		//判断是否insert语句
		if (FillStrategy.INSERT == fillStrategy && SQLType.INSERT != sqlType) {
			return null;
		}
		//判断是否update语句
		if (FillStrategy.UPDATE == fillStrategy && SQLType.UPDATE != sqlType) {
			return null;
		}
		//判断是否insert或update语句
		if (FillStrategy.INSERT_UPDATE == fillStrategy && !sqlType.isUpdate()) {
			return null;
		}
		//判断日期类型
		Class dateType = BeanKit.getPropertyDescriptor(cls,name).getPropertyType();
		Object now;
		if (LocalDateTime.class == dateType) {
			now = LocalDateTime.now();
		} else if (LocalDate.class == dateType) {
			now = LocalDate.now();
		} else if(Timestamp.class == dateType){
			now = new Timestamp(System.currentTimeMillis());
		}else if(java.sql.Date.class == dateType){
			now = new java.sql.Date(System.currentTimeMillis());
		}else if(java.util.Date.class==dateType){
			now = new Date();
		}
		else {
			throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,"期望类型是日期类型，但是 "+cls);
		}
		BeanKit.setBeanProperty(dbValue, now, name);
		return now;

    }

	public  Object toAttr(ExecuteContext ctx, Class cls,String name, ResultSet rs, int index) throws
			SQLException {
		Class dateType = BeanKit.getPropertyDescriptor(cls,name).getPropertyType();
		Object now;
		if (LocalDateTime.class == dateType) {
			now = rs.getTimestamp(index).toLocalDateTime();
		} else if (LocalDate.class == dateType) {
			now =rs.getTimestamp(index).toLocalDateTime().toLocalDate();
		} else if(Timestamp.class == dateType){
			now = rs.getTimestamp(index);
		}else if(java.sql.Date.class == dateType){
			now = rs.getDate(index);
		}else if(java.util.Date.class==dateType){
			now = rs.getDate(index);
		}
		else {
			throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,"期望类型是日期类型，但是 "+cls);
		}
		return now;
	}



}
