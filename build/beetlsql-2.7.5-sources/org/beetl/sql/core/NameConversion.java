package org.beetl.sql.core;

import java.util.ArrayList;
import java.util.List;


public abstract class NameConversion {
	/****
	 * 根据实体class获取表名
	 * @param c
	 * @return
	 */
	public abstract String getTableName(Class<?> c);
	
	
	/**  不一定要实现，主要用于根据表生成java代码
	 * @param tableName
	 * @return
	 */
	public  String getClassName(String tableName){
		return tableName;
	}
	
	public  String getColName(String attrName){
		return getColName(null,attrName);
	}
	/****
	 * 根据class和属性名，获取字段名，此字段必须存在表中，否则返回空
	 * @param c
	 * @param attrName
	 * @return
	 */
	public abstract String getColName(Class<?> c,String attrName);
		/****
	 * 根据class和colName获取属性名
	 * @param c
	 * @param colName
	 * @return
	 */
	public abstract String getPropertyName(Class<?> c,String colName);
	
	public  String getPropertyName(String colName){
		return getPropertyName(null,colName);
	}
	/***
	 *  根据class获取表中所有的id
	 * @return
	 */
	public List<String> getId(){
		List<String> list = new ArrayList<String>();
		list.add("id");
		return list;
	}
	/*
	protected ConnectionSource ds = null;
	protected DatabaseMetaData dbmd = null;*/
}
