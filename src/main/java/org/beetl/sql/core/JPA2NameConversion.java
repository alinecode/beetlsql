package org.beetl.sql.core;


import org.beetl.sql.core.NameConversion;


public class JPA2NameConversion extends NameConversion{

	@Override
	public String getColName(Class<?> c, String attrName) {
		return JPAEntityHelper.getEntityTable(c).getColsMap().get(attrName);
		
	}

	@Override
	public String getPropertyName(Class<?> c, String colName) {
		return JPAEntityHelper.getEntityTable(c).getPropsMap().get(colName);
	}

	@Override
	public String getTableName(Class<?> c) {
		return JPAEntityHelper.getEntityTable(c).getName();
	}

}
