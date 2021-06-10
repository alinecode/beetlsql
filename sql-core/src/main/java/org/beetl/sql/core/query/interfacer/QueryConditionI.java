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

    Query<T> andEq(String column, Object value);

    Query<T> andNotEq(String column, Object value);

    Query<T> andGreat(String column, Object value);

    Query<T> andGreatEq(String column, Object value);

    Query<T> andLess(String column, Object value);

    Query<T> andLessEq(String column, Object value);

    Query<T> andLike(String column, Object value);

    Query<T> andNotLike(String column, Object value);

    Query<T> andIsNull(String column);

    Query<T> andIsNotNull(String column);

    Query<T> andIn(String column, Collection<?> value);

	Query<T> andIn(String column, StrongValue value);

	Query<T> andIn(String column, Optional value);

    Query<T> andNotIn(String column, Collection<?> value);

	Query<T> andNotIn(String column, StrongValue value);

	Query<T> andNotIn(String column, Optional value);

    Query<T> andBetween(String column, Object value1, Object value2);

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
