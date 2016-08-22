package org.beetl.sql.test;

import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.kit.StringKit;

public class DarrenNameConversion extends DefaultNameConversion {
	@Override
	public String getPropertyName(Class<?> c, String colName) {
		//UserID-->userID
		return StringKit.toLowerCaseFirstOne(colName);
		
	}
	
	@Override
	public String getTableName(Class<?> c) {
		//SysUser->sys_user
		Table table = (Table)c.getAnnotation(Table.class);
		if(table!=null){
			return table.name();
		}
		return StringKit.enCodeUnderlined(c.getSimpleName());
	}
	
	@Override
	public  String getClassName(String tableName){
		//SYS_USER --? SysUser
		String clsName = StringKit.deCodeUnderlined(tableName);
		return StringKit.toUpperCaseFirstOne(clsName);
	}
	
	
}
