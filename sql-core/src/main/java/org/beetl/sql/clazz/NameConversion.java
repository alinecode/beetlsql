package org.beetl.sql.clazz;


/**
 * 用于表名，列名和属性，类名的转化,也可以使用注解
 * @author xiandafu
 * @see org.beetl.sql.annotation.entity.Table
 * @see org.beetl.sql.annotation.entity.Column
 */
public abstract class NameConversion {
	/****
	 * 根据实体class获取表名
	 * @param clazz
	 * @return String
	 */
	public abstract String getTableName(Class<?> clazz);


	/**
	 * 不一定要实现，主要用于根据表生成java代码
	 *
	 * @param tableName
	 * @return String
	 */
	public  String getClassName(String tableName){
		return tableName;
	}
	
	public  String getColName(String attrName){
		return getColName(null, attrName);
	}

	/****
	 * 根据class和属性名，获取字段名，此字段必须存在表中，否则返回空
	 * @param c
	 * @param attrName
	 * @return String
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
		return getPropertyName(null, colName);
	}

	protected String getAnnotationColName(Class c, String attrName) {
		if (c == null) {
			return null;
		}
		ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(c);
		String col = classAnnotation.getAttrAnnotationName().get(attrName);
		return col;
	}

	protected String getAnnotationAttrName(Class clazz, String colName) {
		if (clazz == null) {
			return null;
		}
		ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
		String attr = (String) classAnnotation.getColAnnotationName().get(colName);
		return attr;
	}

	protected String getAnnotationTableName(Class clazz) {
		ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
		return classAnnotation.getTableName();
	}

}
