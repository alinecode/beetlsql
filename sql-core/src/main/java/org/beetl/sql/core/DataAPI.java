package org.beetl.sql.core;

import org.beetl.sql.core.engine.template.SQLErrorInfo;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * 操作数据的api，未来考虑都为非JDBC提供统一一个接口
 *
 * @author xiandafu
 * @see SQLManager
 * @see MultipleSQLManager
 */
public interface DataAPI {

    <T> Query<T> query(Class<T> clazz);

    <T> LambdaQuery<T> lambdaQuery(Class<T> clazz);

    SQLErrorInfo validateSqlId(SqlId id);

    <T> List<T> select(SqlId sqlId, Class<T> clazz, Object paras);

    <T> List<T> select(SqlId sqlId, Class<T> clazz);

    <T> PageResult<T> pageQuery(SqlId sqlId, Class<T> clazz, Object paras, PageRequest request);

    <T> T unique(Class<T> clazz, Object pk);

    <T> T single(Class<T> clazz, Object pk);

    <T> List<T> selectByIds(Class<T> clazz, List<?> pks);

    <T> T lock(Class<T> clazz, Object pk);

    <T> List<T> all(Class<T> clazz);

    long allCount(Class<?> clazz);

	<T> List<T> all(Class<T> clazz,Object start,Long pageSize);

    <T> T templateOne(T t);

    <T> List<T> template(T t);

    <T> long templateCount(T t);

    Long longValue(SqlId id, Object paras);

    Integer intValue(SqlId id, Object paras);

    BigDecimal bigDecimalValue(SqlId id, Object paras);

    <T> T selectSingle(SqlId sqlId, Object paras, Class<T> target);

    <T> T selectUnique(SqlId id, Object paras, Class<T> target);

    <T> List<T> select(SqlId sqlId, Object paras, Class<T> clazz, Object start, long size);

    int deleteById(Class<?> clazz, Object pkValue);

    int deleteObject(Object obj);

    int insert(Object paras);

    int insertTemplate(Object paras);

    int insert(Class clazz, Object paras);

    int insertTemplate(Class clazz, Object paras);

    boolean exist(Class<?> clazz, Object pk);

    int[] insertBatch(Class clazz, List<?> list);

    int insert(SqlId sqlId, Object paras);

    Object[] insert(SqlId sqlId, Object paras, String[] cols);

    boolean upsert(Object obj);

    boolean upsertByTemplate(Object obj);

    int updateById(Object obj);

    int updateTemplateById(Object obj);

    int updateTemplateById(Class c, Map paras);

    int updateTemplateById(Class c, Object obj);

    int[] updateByIdBatch(List<?> list);

    int update(SqlId sqlId, Object obj);

    int update(SqlId sqlId);

    int update(SqlId sqlId, Map<String, Object> paras);

    int[] updateBatch(SqlId sqlId, List<?> list);

    int[] updateBatchTemplateById(Class clz, List<?> list);

    int updateAll(Class<?> clazz, Object param);

    <T> List<T> execute(String sqlTemplate, Class<T> clazz, Object paras);

    <T> List<T> execute(String sqlTemplate, Class<T> clazz, Map paras);

    <T> List<T> execute(String sqlTemplate, Class<T> clazz, Object paras, Object start, long size);

    <T> PageResult<T> executePageQuery(String sqlTemplate, Class<T> clazz, Object paras, PageRequest<T> request);

    int executeUpdate(String sqlTemplate, Object paras);

    <T> List<T> execute(SQLReady p, Class<T> clazz);
    <T> T executeQueryOne(SQLReady p, Class<T> clazz);

    <T> StreamData<T> streamExecute(SQLReady p, Class<T> clazz);

    <T> StreamData<T> streamExecute(String sqlTemplate, Class<T> clazz,Object para);

    <T> StreamData<T> stream(SqlId sqlId,  Class<T> clazz,Object paras);

    <T> PageResult<T> execute(SQLReady p, Class<T> clazz, PageRequest<T> pageRequest);

    int executeUpdate(SQLReady p);

    int[] executeBatchUpdate(SQLBatchReady batch);
}
