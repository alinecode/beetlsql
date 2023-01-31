package org.beetl.sql.core.query.interfacer;

import org.beetl.sql.core.query.Query;
import org.beetl.sql.core.query.QueryCondition;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author GavinKing
 */
public interface QueryConditionI<T> {
	/**
	 * <pre>
	 *     column=value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andEq(String column, Object value);

	/**
	 * <pre>
	 *     column!=value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andNotEq(String column, Object value);
	/**
	 * <pre>
	 *     column>value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andGreat(String column, Object value);
	/**
	 * <pre>
	 *     column>=value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andGreatEq(String column, Object value);
	/**
	 * <pre>
	 *     column<value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andLess(String column, Object value);
	/**
	 * <pre>
	 *     column<=value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andLessEq(String column, Object value);
	/**
	 * <pre>
	 *     column like value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andLike(String column, Object value);
	/**
	 * <pre>
	 *     column not like value
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andNotLike(String column, Object value);

	/**
	 * <pre>
	 *     column is null
	 * </pre>
	 * @param column
	 * @return
	 */
    Query<T> andIsNull(String column);

	/**
	 * <pre>
	 *     column is not null
	 * </pre>
	 * @param column
	 * @return
	 */
    Query<T> andIsNotNull(String column);
	/**
	 * <pre>
	 *     column in {value...}
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andIn(String column, Collection<?> value);

	Query<T> andIn(String column, StrongValue value);

	Query<T> andIn(String column, Optional value);
	/**
	 * <pre>
	 *     column not in {value...}
	 * </pre>
	 * @param column
	 * @param value
	 * @return
	 */
    Query<T> andNotIn(String column, Collection<?> value);

	Query<T> andNotIn(String column, StrongValue value);

	Query<T> andNotIn(String column, Optional value);

	/**
	 * <pre>
	 *     column betwwen value1 and value2
	 * </pre>
	 * @param column
	 * @param value1
	 * @param value2
	 * @return
	 */
    Query<T> andBetween(String column, Object value1, Object value2);

	/**
	 * <pre>
	 *     column not betwwen value1 and value2
	 * </pre>
	 * @param column
	 * @param value1
	 * @param value2
	 * @return
	 */
    Query<T> andNotBetween(String column, Object value1, Object value2);

	
    Query<T> orEq(String column, Object value);

    Query<T> orNotEq(String column, Object value);

    Query<T> orGreat(String column, Object value);

    Query<T> orGreatEq(String column, Object value);

    Query<T> orLess(String column, Object value);

    Query<T> orLessEq(String column, Object value);

    Query<T> orLike(String column, Object value);

    Query<T> orNotLike(String column, Object value);

    Query<T> orIsNull(String column);

    Query<T> orIsNotNull(String column);

    Query<T> orIn(String column, Collection<?> value);

    Query<T> orIn(String column, StrongValue value);

	Query<T> orIn(String column, Optional value);

    Query<T> orNotIn(String column, Collection<?> value);

    Query<T> orNotIn(String column, StrongValue value);

	Query<T> orNotIn(String column, Optional value);

    Query<T> orBetween(String column, Object value1, Object value2);

    Query<T> orNotBetween(String column, Object value1, Object value2);


    /**
     * 多条件组合 and
     *
     * @param condition
     * @return
     */

    Query<T> and(QueryCondition condition);

    /***
     * 多条件组合 or
     * @param condition
     * @return
     */

    Query<T> or(QueryCondition condition);

	/**
	 * 指定一个表名字，而不是T对应的表
	 * @param tableName
	 * @return
	 */
	Query<T> asTable(String tableName);

	/**
	 * 指示表名是个表达式，需要映射成真实表
	 * @return
	 */
	Query<T> virtualTable();


    /**
     * 获取sql
     *
     * @return
     */
    StringBuilder getSql();

    /**
     * 设置sql
     *
     * @param sql
     */
    void setSql(StringBuilder sql);

    /***
     * 获取参数
     * @return
     */
    List<Object> getParams();

    Query<T> distinct();
}
