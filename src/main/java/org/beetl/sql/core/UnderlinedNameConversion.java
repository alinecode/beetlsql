package org.beetl.sql.core;

import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.kit.StringKit;

/***
 *  下划线命名转换
 * 数据库 Sys_User,对应类SysUser,列user_Id,对应属性userId
 * @author xiandafu
 * @author Gavin
 *
 */
public class UnderlinedNameConversion extends  NameConversion {
	@Override
	public String getTableName(Class<?> c) {
		Table table = (Table)c.getAnnotation(Table.class);
		if(table!=null){
			return table.name();
		}
		return StringKit.enCodeUnderlined(c.getSimpleName());
	}
	
	public  String getClassName(String tableName){
		 String temp = StringKit.deCodeUnderlined(tableName.toLowerCase());
		 return StringKit.toUpperCaseFirstOne(temp);
		 
	}
	
	@Override
	public String getColName(Class<?> c,String attrName) {
		String col = super.getAnnotationColName(c,attrName);
		if(col!=null){
			return col;
		}
		return StringKit.enCodeUnderlined(attrName);
	}

	

	@Override
	public String getPropertyName(Class<?> c,String colName) {
		String attrName = super.getAnnotationAttrName(c,colName);
		if(attrName!=null){
			return attrName;
		}
		return StringKit.deCodeUnderlined(colName.toLowerCase());
	}
}
