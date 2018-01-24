package org.beetl.sql.core.query.interfacer;


import org.beetl.sql.core.query.QueryCondition;

/**
 * @author GavinKing
 *
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
    
    T asc(String column);
    
    T desc(String column);

    /***
     * limit 子句
     * @param startRow 开始行数（包含）
     * @param pageSize
     * @return
     */
    T limit(long startRow, long pageSize);

}
