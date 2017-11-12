package org.beetl.sql.core.query.interfacer;


import java.util.List;

/**
 * @ClassName: QueryExecuteI
 * @Description:
 * @author GavinKing
 * @date 2017/11/12
 *
 */
public interface QueryExecuteI<M>{

    /***
     * 指定字段查询
     * @param columns
     * @return 查询结果
     */
    List<M> select(String... columns);

    /**
     * 查询所有字段
     *
     * @return 查询结果
     */
    List<M> select();

    /***
     * 全部更新，包括更新null值
     * @param m
     * @return 影响的行数
     */
    int update(M m);

    /***
     * 有选择的更新
     * @param m
     * @return 影响的行数
     */
    int updateSelective(M m);

    /***
     * 全部插入，包括插入null值
     * @param m
     * @return 影响的行数
     */
    int insert(M m);

    /***
     * 有选择的插入，null不插入
     * @param m
     * @return 影响的行数
     */
    int insertSelective(M m);

    /***
     * 删除
     * @return 影响的行数
     */
    int delete();

    /***
     * count
     * @return 总行数
     */
    int count();
}
