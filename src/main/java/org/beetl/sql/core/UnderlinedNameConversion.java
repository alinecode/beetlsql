package org.beetl.sql.core;

import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.kit.StringKit;

/***
 *  下划线命名转换
 * 
 * @author Gavin
 *
 */
public class UnderlinedNameConversion extends NameConversion {
	@Override
	public String getTableName(Class<?> c) {
		Table table = (Table)c.getAnnotation(Table.class);
		if(table!=null){
			return table.name();
		}
		return StringKit.enCodeUnderlined(c.getSimpleName());
	}
	@Override
	public String getColName(Class<?> c,String attrName) {
		return StringKit.enCodeUnderlined(attrName);
	}

	

	@Override
	public String getPropertyName(Class<?> c,String colName) {
		return StringKit.deCodeUnderlined(colName);
	}
}
