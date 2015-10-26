package org.beetl.sql.core;

import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.kit.StringKit;

/***
 * 驼峰命名转换
 * 
 * @author Gavin
 *
 */
@Deprecated
public class HumpNameConversion extends NameConversion {
	@Override
	public String getTableName(Class<?> c) {
		Table table = (Table)c.getAnnotation(Table.class);
		if(table!=null){
			return table.name();
		}
		return StringKit.toLowerCaseFirstOne(c.getSimpleName());
	}
	@Override
	public String getColName(Class<?> c,String attrName) {
		return StringKit.toLowerCaseFirstOne(attrName);
	}

	

	@Override
	public String getPropertyName(Class<?> c,String colName) {
		return StringKit.toLowerCaseFirstOne(colName);
	}

}
