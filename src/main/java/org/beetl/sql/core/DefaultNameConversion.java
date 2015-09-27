package org.beetl.sql.core;

import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.kit.StringKit;

public class DefaultNameConversion extends NameConversion {

	@Override
	public String getTableName(Class<?> c) {
		Table table = (Table)c.getAnnotation(Table.class);
		if(table!=null){
			return table.name();
		}
		return c.getSimpleName();
	}

	@Override
	public String getColName(Class<?> c, String attrName) {
		return attrName;
	}

	@Override
	public String getPropertyName(Class<?> c, String colName) {
		return colName;
	}

}
