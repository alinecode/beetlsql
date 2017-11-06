package org.beetl.sql.core.query;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName: QueryConditionInterFace
 * @Description:查询条件接口
 * @author GavinKing
 * @date 2017/11/5
 *
 */
public interface QueryConditionInterFace<T extends QueryConditionInterFace> {
    T andEq(String column, Object value);
    T andNotEq(String column, Object value);
    T andGt(String column, Object value);
    T andGEq(String column, Object value);
    T andLt(String column, Object value);
    T andLEq(String column, Object value);
    T andLike(String column, String value);
    T andNotLike(String column, String value);
    T andIsNull(String column);
    T andIsNotNull(String column);
    T andExists(String column);
    T andNotExists(String column);
    T andIn(String column, Collection<?> value);
    T andNotIn(String column, Collection<?> value);
    T andBetween(String column, Collection<?> value);
    T andNotBetween(String column, Collection<?> value);


    T orEq(String column, Object value);
    T orNotEq(String column, Object value);
    T orGt(String column, Object value);
    T orGEq(String column, Object value);
    T orLt(String column, Object value);
    T orLEq(String column, Object value);
    T orLike(String column, String value);
    T orNotLike(String column, String value);
    T orIsNull(String column);
    T orIsNotNull(String column);
    T orExists(String column);
    T orNotExists(String column);
    T orIn(String column, Collection<?> value);
    T orNotIn(String column, Collection<?> value);
    T orBetween(String column, Collection<?> value);
    T orNotBetween(String column, Collection<?> value);

    /**
     * 多条件组合 and
     * @param condition
     * @return
     */
    T and(T condition);

    /***
     * 多条件组合 or
     * @param condition
     * @return
     */
    T or(T condition);


    /**
     * 获取sql
     * @return
     */
    StringBuilder getSql();

    /***
     * 获取参数
     * @return
     */
    List<Object> getParams();
}
