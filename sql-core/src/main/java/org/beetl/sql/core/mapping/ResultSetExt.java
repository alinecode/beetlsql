/**
 *
 */
package org.beetl.sql.core.mapping;

import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author suxinjie
 *
 * resultSet扩展，用于对ResultSet进行额外处理
 * 比如RowMapper功能
 *
 */
public interface ResultSetExt<T> {

	/**
	 * 处理ResultSet，实现扩展
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	T handleResultSet(ExecuteContext ctx, ResultSet rs, Class<?> z, Annotation config) throws SQLException;

}
