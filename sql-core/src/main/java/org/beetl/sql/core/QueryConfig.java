package org.beetl.sql.core;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.mapping.ResultSetMapper;
import org.beetl.sql.core.mapping.RowMapper;

/**
 * 每次查询的相关配置信息
 */
public class QueryConfig {
	/**
	 * 对应{@link SqlId#type} 的view类型的查询
	 */
	Class viewClass;
	/**
	 * 单行映射类
	 */
	Class rowMapperClass;
	/**
	 * 结果集映射类
	 */
	Class resultSetClass;

	public Class getViewClass() {
		return viewClass;
	}

	public void setViewClass(Class viewClass) {
		this.viewClass = viewClass;
	}

	public RowMapper getRowMapper() {
		if (rowMapperClass == null) {
			return null;
		}
		return (RowMapper) BeanKit.newSingleInstance(rowMapperClass);
	}

	public void setRowMapperClass(Class rowMapperClass) {
		this.rowMapperClass = rowMapperClass;
	}

	public ResultSetMapper getResultSetMapper() {

		if (resultSetClass == null) {
			return null;
		}
		return (ResultSetMapper) BeanKit.newSingleInstance(resultSetClass);
	}

	public void setResultSetClass(Class resultSetClass) {
		this.resultSetClass = resultSetClass;
	}

	public void clear() {
		viewClass = null;
		rowMapperClass = null;
		resultSetClass = null;
	}
}
