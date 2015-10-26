package org.beetl.sql.core;

import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.kit.StringKit;

/** 数据库命名完全按照java风格来，比如，数据库
 *   表 SysUser,对应类SysUser,列userId,对应属性userId
 * @author xiandafu
 *
 */
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
