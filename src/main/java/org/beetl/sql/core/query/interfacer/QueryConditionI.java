package org.beetl.sql.core.query.interfacer;

import org.beetl.sql.core.query.Query;
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
public interface QueryConditionI {
    Query andEq(String column, Object value);
    Query andNotEq(String column, Object value);
    Query andGreat(String column, Object value);
    Query andGreatEq(String column, Object value);
    Query andLess(String column, Object value);
    Query andLessEq(String column, Object value);
    Query andLike(String column, String value);
    Query andNotLike(String column, String value);
    Query andIsNull(String column);
    Query andIsNotNull(String column);
    Query andIn(String column, Collection<?> value);
    Query andNotIn(String column, Collection<?> value);
    Query andBetween(String column, Object value1,Object value2);
    Query andNotBetween(String column, Object value1,Object value2);


    Query orEq(String column, Object value);
    Query orNotEq(String column, Object value);
    Query orGreat(String column, Object value);
    Query orGreatEq(String column, Object value);
    Query orLess(String column, Object value);
    Query orLessEq(String column, Object value);
    Query orLike(String column, String value);
    Query orNotLike(String column, String value);
    Query orIsNull(String column);
    Query orIsNotNull(String column);
    Query orIn(String column, Collection<?> value);
    Query orNotIn(String column, Collection<?> value);
    Query orBetween(String column, Object value1,Object value2);
    Query orNotBetween(String column, Object value1,Object value2);

    /**
     * 多条件组合 and
     * @param condition
     * @return
     */
    Query and(QueryCondition condition);

    /***
     * 多条件组合 or
     * @param condition
     * @return
     */
    Query or(QueryCondition condition);


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
