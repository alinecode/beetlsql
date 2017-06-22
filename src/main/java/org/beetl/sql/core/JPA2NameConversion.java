package org.beetl.sql.core;


import java.util.Map;


public class JPA2NameConversion extends NameConversion{ 
	
	NameConversion nc = null;
	public JPA2NameConversion(){
		nc=new DefaultNameConversion();
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
		if(c==null||Map.class.isAssignableFrom(c)){
			return nc!=null?nc.getColName(attrName):attrName;
		}
		return JPAEntityHelper.getEntityTable(c,nc).getCol(attrName);
	}

	@Override
	public String getPropertyName(Class<?> c, String colName) {
		if(c==null||Map.class.isAssignableFrom(c)){
			return nc!=null?nc.getPropertyName(c, colName):colName;
		}
		
		//col到property是可能有对应关系的，即使property被标注了Transient
		String name =  JPAEntityHelper.getEntityTable(c,nc).getProp(colName);
		if(name!=null){
			return name;
		}else{
			return nc!=null?nc.getPropertyName(c, colName):colName;
		}
	}

	@Override
	public String getTableName(Class<?> c) {
		return JPAEntityHelper.getEntityTable(c,nc).getName();
	}

}
