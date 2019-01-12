package org.beetl.sql.core.mapper;

import java.util.List;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;

/**
 * BaseMapper.
 *
 * @param <T> the generic type
 */
public interface BaseMapper<T> {

    /**
     * 通用插入，插入一个实体对象到数据库，所以字段将参与操作，除非你使用ColumnIgnore注解
     *
     * @param entity
     */
    void insert(T entity);

    /**
     * （数据库表有自增主键调用此方法）如果实体对应的有自增主键，插入一个实体到数据库，设置assignKey为true的时候，将会获取此主键
     *
     * @param entity
     * @param autDbAssignKey 是否获取自增主键
     */
    void insert(T entity, boolean autDbAssignKey);

    /**
     * 插入实体到数据库，对于null值不做处理
     *
     * @param entity
     */
    void insertTemplate(T entity);

    /**
     * 如果实体对应的有自增主键，插入实体到数据库，对于null值不做处理,设置assignKey为true的时候，将会获取此主键
     *
     * @param entity
     * @param autoDbAssignKey
     */
    void insertTemplate(T entity, boolean autoDbAssignKey);

    /**
     * 批量插入实体。此方法不会获取自增主键的值，如果需要，建议不适用批量插入，适用
     * <pre>
     * insert(T entity,true);
     * </pre>
     *
     * @param list
     */
    void insertBatch(List<T> list);

    /**
     * （数据库表有自增主键调用此方法）如果实体对应的有自增主键，插入实体到数据库，自增主键值放到keyHolder里处理
     *
     * @param entity
     * @return
     */
    KeyHolder insertReturnKey(T entity);

    /**
     * 根据主键更新对象，所以属性都参与更新。也可以使用主键ColumnIgnore来控制更新的时候忽略此字段
     *
     * @param entity
     * @return
     */
    int updateById(T entity);

    /**
     * 根据主键更新对象，只有不为null的属性参与更新
     *
     * @param entity
     * @return
     */
    int updateTemplateById(T entity);

    /**
     * 更新或插入。不管是更新还是插入，皆不更新/插入null值。
     * 如果是更新操作，则根据主键进行更新。
     * 如果是插入操作，将主键返回到实体中。
     * @param entity 待更新/插入的实体对象
     * @return 受影响条数
     */
    int upsert(T entity);

    /**
     * 根据主键删除对象，如果对象是复合主键，传入对象本生即可
     *
     * @param key
     * @return
     */
    int deleteById(Object key);


    /**
     * 根据主键获取对象，如果对象不存在，则会抛出一个Runtime异常
     *
     * @param key
     * @return
     */
    T unique(Object key);

    /**
     * 根据主键获取对象，如果对象不存在，返回null
     *
     * @param key
     * @return
     */
    T single(Object key);


    /**
     * 根据主键获取对象，如果在事物中执行会添加数据库行级锁(select * from table where id = ? for update)，如果对象不存在，返回null
     *
     * @param key
     * @return
     */
    T lock(Object key);

    /**
     * 返回实体对应的所有数据库记录
     *
     * @return
     */
    List<T> all();

    /**
     * 返回实体对应的一个范围的记录
     *
     * @param start
     * @param size
     * @return
     */
    List<T> all(int start, int size);

    /**
     * 返回实体在数据库里的总数
     *
     * @return
     */
    long allCount();

    /**
     * 模板查询，返回符合模板得所有结果。beetlsql将取出非null值（日期类型排除在外），从数据库找出完全匹配的结果集
     *
     * @param entity
     * @return
     */
    List<T> template(T entity);


    /**
     * 模板查询，返回一条结果,如果没有，返回null
     *
     * @param entity
     * @return
     */
    <T> T templateOne(T entity);

    List<T> template(T entity, int start, int size);

    void templatePage(PageQuery<T> query);

    /**
     * 符合模板得个数
     *
     * @param entity
     * @return
     */
    long templateCount(T entity);


    /**
     * 执行一个jdbc sql模板查询
     *
     * @param sql
     * @param args
     * @return
     */
    List<T> execute(String sql, Object... args);

    /**
     * 执行一个更新的jdbc sql
     *
     * @param sql
     * @param args
     * @return
     */
    int executeUpdate(String sql, Object... args);

    SQLManager getSQLManager();

    /**
     * 返回一个Query对象
     *
     * @return
     */
    Query<T> createQuery();


    /**
     * 返回一个LambdaQuery对象
     *
     * @return
     */
    LambdaQuery<T> createLambdaQuery();
}
