package org.beetl.sql.core;


import java.util.Map;

import org.beetl.sql.core.NameConversion;


public class JPA2NameConversion extends NameConversion{ 
	
	NameConversion nc = null;
	public JPA2NameConversion(){
		
	}
	/**
	 * 对于没有jpa注解的，采用的命名策略，包括tail的命名策略，如果nc为null，则直接返回列名
	 * @param nc
	 */
	public JPA2NameConversion(NameConversion nc){
		this.nc = nc ;
	}
	
	@Override
	public String getColName(Class<?> c, String attrName) {
		if(Map.class.isAssignableFrom(c)){
			return nc!=null?nc.getColName(attrName):attrName;
			
		}
		String colName =  JPAEntityHelper.getEntityTable(c).getColsMap().get(attrName);
		if(colName!=null){
			return colName;
		}else{
			return nc!=null?nc.getColName(attrName):attrName;
		}
		
	}

	@Override
	public String getPropertyName(Class<?> c, String colName) {
		if(Map.class.isAssignableFrom(c)){
			return nc!=null?nc.getPropertyName(c, colName):colName;
			
		}
		String name =  JPAEntityHelper.getEntityTable(c).getPropsMap().get(colName);
		if(name!=null){
			return name;
		}else{
			return nc!=null?nc.getPropertyName(c, colName):colName;
		}
	}

	@Override
	public String getTableName(Class<?> c) {
		return JPAEntityHelper.getEntityTable(c).getName();
	}

}
