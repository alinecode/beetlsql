package org.beetl.sql.clazz;


/**
 * 用于表名，列名和属性，类名的转化,也可以使用注解
 * @see org.beetl.sql.annotation.entity.Table
 * @see org.beetl.sql.annotation.entity.Column
 * @author xiandafu
 */
public abstract class NameConversion {
	/****
	 * 根据实体class获取表名
	 * @param clazz 实体类
	 * @return String 对应表名
	 */
	public abstract String getTableName(Class<?> clazz);


	/**
	 * 根据表名生成对应java实体类名称
	 * <br/>
	 * 注：非必须实现
	 *
	 * @param tableName 表名
	 * @return String 对应实体类名
	 */
	public String getClassName(String tableName) {
		return tableName;
	}

	/**
	 * Java实体类字段名称转数据表列名
	 * @param attrName 实体类属性名
	 * @return 转换后列名
	 */
	public String getColName(String attrName) {
		return getColName(null, attrName);
	}

	/**
	 * 根据class和属性名，获取字段名，此字段必须存在表中，否则返回空
	 * @param c 属性所在类
	 * @param attrName 属性名称
	 * @return String 列名
	 */
	public abstract String getColName(Class<?> c, String attrName);

	/**
	 * todo ask
	 * 根据class和colName获取属性名，转换后属性名必须存在于类中
	 * @param c 转换后实体类
	 * @param colName 数据列名
	 * @return 返回存在的属性名
	 */
	public abstract String getPropertyName(Class<?> c, String colName);

	public String getPropertyName(String colName) {
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
