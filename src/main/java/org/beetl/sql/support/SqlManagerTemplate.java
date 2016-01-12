package org.beetl.sql.support;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.kit.CommonKit;

/**
 * SqlManagerTemplate.
 * 
 * 
 * @author zhoupan.
 */
public class SqlManagerTemplate {

	/** The sql manager. */
	protected SQLManager sqlManager;

	/**
	 * Gets the sql manager.
	 *
	 * @return the sql manager
	 */
	public SQLManager getSqlManager() {
		CommonKit.throwIfNull(this.sqlManager, "sqlManager not allow null.");
		return sqlManager;
	}

	/**
	 * Sets the sql manager.
	 *
	 * @param sqlManager
	 *            the sql manager
	 */
	public void setSqlManager(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	/**
	 * Select list.
	 *
	 * @param <E>
	 *            the element type
	 * @param statement
	 *            the statement
	 * @param clazz
	 *            the clazz
	 * @param paramMap
	 *            the param map
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the list< e>
	 */
	public <E> List<E> selectList(String statement, Class<E> clazz, Map<String, Object> paramMap, int offset,
			int limit) {
		return this.getSqlManager().select(statement, clazz, paramMap, offset, limit);
	}

	/**
	 * Select list.
	 *
	 * @param <E>
	 *            the element type
	 * @param statement
	 *            the statement
	 * @param clazz
	 *            the clazz
	 * @param parameter
	 *            the parameter
	 * @return the list< e>
	 */
	public <E> List<E> selectList(String statement, Class<E> clazz, Object parameter) {
		return this.getSqlManager().select(statement, clazz, parameter);
	}

	/**
	 * Insert.
	 *
	 * @param statement
	 *            the statement
	 * @param parameter
	 *            the parameter
	 * @return the int
	 */
	public int insert(String statement, Object parameter) {
		return this.getSqlManager().update(statement, parameter);
	}

	/**
	 * Update.
	 *
	 * @param statement
	 *            the statement
	 * @param parameter
	 *            the parameter
	 * @return the int
	 */
	public int update(String statement, Object parameter) {
		return this.getSqlManager().update(statement, parameter);
	}

	/**
	 * Delete.
	 *
	 * @param statement
	 *            the statement
	 * @param parameter
	 *            the parameter
	 * @return the int
	 */
	public int delete(String statement, Object parameter) {
		return this.getSqlManager().update(statement, parameter);
	}

	/**
	 * Delete.
	 *
	 * @param statement
	 *            the statement
	 * @param parameter
	 *            the parameter
	 * @return the int
	 */
	public int delete(String statement, Map<String, Object> parameter) {
		return this.getSqlManager().update(statement, parameter);
	}

	/**
	 * Select one.
	 *
	 * @param <E>
	 *            the element type
	 * @param statement
	 *            the statement
	 * @param clazz
	 *            the clazz
	 * @param parameter
	 *            the parameter
	 * @return the e
	 */
	public <E> E selectOne(String statement, Class<E> clazz, Object parameter) {
		return this.getSqlManager().selectSingle(statement, parameter, clazz);
	}

	/**
	 * Select one.
	 *
	 * @param <E>
	 *            the element type
	 * @param statement
	 *            the statement
	 * @param clazz
	 *            the clazz
	 * @param parameter
	 *            the parameter
	 * @return the e
	 */
	public <E> E selectOne(String statement, Class<E> clazz, Map<String, Object> parameter) {
		return this.getSqlManager().selectSingle(statement, parameter, clazz);
	}

}
