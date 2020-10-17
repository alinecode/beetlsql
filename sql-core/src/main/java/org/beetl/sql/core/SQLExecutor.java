package org.beetl.sql.core;

import org.beetl.sql.core.engine.template.TemplateContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库和非数据库 调用api
 */
public interface SQLExecutor {

    /**
     * 插入一条记录到clazz指定的表，参数是paras，clazz如果申明了自增，序列，或者其他数据库返回值，
     * 插入后，会取出这些值赋值到paras对象里
     * @param target
     * @param paras
     * @return
     */
    int insert(Class target, Object paras);




    /**
     * 插入数据，cols表明需要获取的返回数据，比如自增id，或者其他数据库自动生成数据
     *
     * @param paras
     * @param cols
     * @return
     */
    Object[] insert(Class target,Object paras, String[] cols);

    /**
     * 获得一条记录，如果有多条，取第一条，如果没有，则返回null
     * @param paras
     * @param target
     * @param <T>
     * @return
     */
    <T> T singleSelect(Class<T> target,Object paras);

    /**
     * 获取一条记录，如果有多条或者没有，抛异常
     * @param paras
     * @param target
     * @param <T>
     * @return
     */
    <T> T selectUnique(Class<T> target,Object paras);

    /**
     * 查询，映射结果到clazz里
     * @param target
     * @param paras
     * @param <T>
     * @return
     */
    <T> List<T> select(Class<T> target, Object paras);


    /**
     * 将JDBC结果集映射到clazz，所有查询api都调用此接口做映射
     * @param rs
     * @param target
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T> List<T> mappingSelect(Class<T> target,ResultSet rs) throws SQLException;

    /**
     * 范围查找
     * @param paras
     * @param target
     * @param start
     * @param size
     * @param <T>
     * @return
     */
    <T> List<T> select( Class<T> target,Object paras, Object start, long size);

    /**
     *
     * @param paras
     * @return
     */
    long selectCount(Object paras);


    /**
     * 更新对象
     * @param target
     * @param obj
     * @return
     */
    int update(Class target,Object obj);

    int[] updateBatch(List<?> list);

    int[] updateBatch(Class<?> target ,List<?> list);

    int[] insertBatch(Class<?> target,List<?> list);


    <T> T unique(Class<T> target, Object objId);

    <T> T single(Class<T> target, Object objId);

    boolean existById(Class target, Object objId);


    int deleteById(Class<?> target, Object objId);

    <T> List<T> sqlReadySelect(Class<T> target, SQLReady p);

    int sqlReadyExecuteUpdate(SQLReady p);

    int[] sqlReadyBatchExecuteUpdate(SQLBatchReady batch);

    /**
     * 执行sql模板，得到sql语句和参数
     * @param parasMap
     * @return
     */
    SQLResult run(Map<String, Object> parasMap  );

    SQLResult run(Map<String, Object> parasMap , TemplateContext ctx);


    Map beforeExecute(Class target, Object paras,boolean isUpdate);

    ExecuteContext getExecuteContext();



}
