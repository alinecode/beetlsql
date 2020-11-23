package org.beetl.sql.core;

import org.beetl.sql.clazz.NameConversion;

/**
 * 数据库命名完全按照java风格来，比如，数据库
 *  表 SysUser,对应类SysUser,列userId,对应属性userId
 * @author xiandafu
 *
 */
public class DefaultNameConversion extends NameConversion {

	@Override
	public String getTableName(Class<?> clazz) {
		String name = getAnnotationTableName(clazz);
		if(name!=null){
			return name;
		}
		return clazz.getSimpleName();
	}

	@Override
	public String getColName(Class<?> clazz, String attrName) {

		String col = super.getAnnotationColName(clazz,attrName);
		if(col!=null){
			return col;
		}
		return attrName;
	}

	@Override
	public String getPropertyName(Class<?> clazz, String colName) {
		String attrName = super.getAnnotationAttrName(clazz,colName);
		if(attrName!=null){
			return attrName;
		}
		return colName;
	}

}
