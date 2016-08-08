package org.beetl.sql.core;

import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.kit.StringKit;

/***
 *  下划线命名转换,注:不在使用,使用UnderlinedNameConversion
 * 数据库 SYS_USER,对应类SysUser,列USER_ID,对应属性userId
 * @author woate
 *
 */
@Deprecated
public class UpperCaseUnderlinedNameConversion extends NameConversion {
	@Override
	public String getTableName(Class<?> c) {
		Table table = (Table)c.getAnnotation(Table.class);
		if(table!=null){
			return table.name();
		}
		return StringKit.enCodeUnderlined(c.getSimpleName()).toUpperCase();
	}
	
	public  String getClassName(String tableName){
		 String temp = StringKit.deCodeUnderlined(tableName.toLowerCase());
		 return StringKit.toUpperCaseFirstOne(temp);
		 
	}
	
	@Override
	public String getColName(Class<?> c,String attrName) {
		
		return StringKit.enCodeUnderlined(attrName).toUpperCase();
	}

	

	@Override
	public String getPropertyName(Class<?> c,String colName) {
		return StringKit.deCodeUnderlined(colName.toLowerCase());
	}
}
