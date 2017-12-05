package org.beetl.sql.core.query.interfacer;

import org.beetl.sql.core.query.Java6Query;
import org.beetl.sql.core.query.QueryCondition;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName: QueryConditionI
 * @Description:查询条件接口
 * @author GavinKing
 * @date 2017/11/5
 *
 */
public interface QueryConditionI<T> {
    Java6Query<T> andEq(String column, Object value);
    Java6Query<T> andNotEq(String column, Object value);
    Java6Query<T> andGreat(String column, Object value);
    Java6Query<T> andGreatEq(String column, Object value);
    Java6Query<T> andLess(String column, Object value);
    Java6Query<T> andLessEq(String column, Object value);
    Java6Query<T> andLike(String column, String value);
    Java6Query<T> andNotLike(String column, String value);
    Java6Query<T> andIsNull(String column);
    Java6Query<T> andIsNotNull(String column);
    Java6Query<T> andIn(String column, Collection<?> value);
    Java6Query<T> andNotIn(String column, Collection<?> value);
    Java6Query<T> andBetween(String column, Object value1,Object value2);
    Java6Query<T> andNotBetween(String column, Object value1,Object value2);


    Java6Query<T> orEq(String column, Object value);
    Java6Query<T> orNotEq(String column, Object value);
    Java6Query<T> orGreat(String column, Object value);
    Java6Query<T> orGreatEq(String column, Object value);
    Java6Query<T> orLess(String column, Object value);
    Java6Query<T> orLessEq(String column, Object value);
    Java6Query<T> orLike(String column, String value);
    Java6Query<T> orNotLike(String column, String value);
    Java6Query<T> orIsNull(String column);
    Java6Query<T> orIsNotNull(String column);
    Java6Query<T> orIn(String column, Collection<?> value);
    Java6Query<T> orNotIn(String column, Collection<?> value);
    Java6Query<T> orBetween(String column, Object value1,Object value2);
    Java6Query<T> orNotBetween(String column, Object value1,Object value2);

    /**
     * 多条件组合 and
     * @param condition
     * @return
     */
    Java6Query<T> and(QueryCondition condition);

    /***
     * 多条件组合 or
     * @param condition
     * @return
     */
    Java6Query<T> or(QueryCondition condition);


    /**
     * 获取sql
     * @return
     */
    StringBuilder getSql();

    /**
     * 设置sql
     * @param sql
     */
    void setSql(StringBuilder sql);

    /***
     * 获取参数
     * @return
     */
    List<Object> getParams();
}
