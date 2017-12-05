package org.beetl.sql.core.query.interfacer;


import org.beetl.sql.core.query.QueryCondition;

/**
 * @author GavinKing
 * @ClassName: QueryOtherI
 * @Description: 查询器接口
 * @date 2017/11/5
 */
public interface QueryOtherI<T extends QueryOtherI>{

    /**
     * having子句
     * @param condition
     * @return
     */
    T having(QueryCondition condition);

    /***
     * groupBy 子句
     * @param column
     * @return
     */
    T groupBy(String column);

    /***
     * orderBy 子句
     * 例如 orderBy id desc,user_id asc
     * @param orderBy
     * @return
     */
    T orderBy(String orderBy);

    /***
     * limit 子句
     * @param startRow 开始行数（包含）
     * @param pageSize
     * @return
     */
    T limit(long startRow, long pageSize);

}
