package org.beetl.sql.core.query;

/**
 * @author GavinKing
 * @ClassName: QueryBaseInterFace
 * @Description: 查询器接口
 * @date 2017/11/5
 */
public interface QueryBaseInterFace<T extends QueryBaseInterFace>{

    /***
     * 指定字段查询
     * @param columns
     * @return
     */
    T select(String... columns);

    /**
     * 查询所有字段
     *
     * @return
     */
    T select();

    /***
     * 指定表名查询
     * @param table
     * @return
     */
    T from(String table);

    /**
     * 按照泛型类对应的表查询
     *
     * @return
     */
    T from();

    /**
     * having子句
     * @param condition
     * @return
     */
    T having(T condition);

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

}
