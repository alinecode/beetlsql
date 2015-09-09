package org.beetl.sql.core.mapping;

/**  
 * 扩展：废弃
 */
public interface Matcher {


	 /**
	 * @param columnName
	 * @param propertyName
	 * @return
	 */
	boolean match(String columnName, String propertyName);
	 
}
