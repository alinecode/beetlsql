package org.beetl.sql.mapper;

import org.beetl.ext.fn.StringUtil;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.internal.*;

import java.util.List;
import java.util.Set;

/**
 * BaseMapper.定义了一个Mapper接口，并内置了多个方法
 * 开发者可以定义自己的"BaseMapper",并使用AutoMapper注解来申明这是内置的，beetlsql会调用其注解使用的接口
 *
 * @param <T> the generic type
 * @author xiandafu
 */
public interface BaseMapper<T> {

	/**
	 * 通用插入，插入一个实体对象到数据库，所以字段将参与操作，除非你使用ColumnIgnore注解
	 * SqlResource
	 *
	 * @param entity 实体
	 */
	@AutoMapper(InsertAMI.class)
	void insert(T entity);

	/**
	 * 插入实体到数据库，对于null值不做处理
	 *
	 * @param entity 实体
	 */
	@AutoMapper(InsertTemplateAMI.class)
	void insertTemplate(T entity);

	/**
	 * 批量插入实体。此方法不会获取自增主键的值，如果需要，建议不适用批量插入，适用
	 * <pre>
	 * insert(T entity,true);
	 * </pre>
	 *
	 * @param list 实体集合
	 */
	@AutoMapper(InsertBatchAMI.class)
	void insertBatch(List<T> list);

	/**
	 * 根据主键更新对象，所以属性都参与更新。也可以使用主键ColumnIgnore来控制更新的时候忽略此字段
	 *
	 * @param entity 实体
	 * @return int
	 */
	@AutoMapper(UpdateByIdAMI.class)
	int updateById(T entity);

	/**
	 * 根据主键更新对象，只有不为null的属性参与更新
	 *
	 * @param entity 实体
	 * @return int
	 */
	@AutoMapper(UpdateTemplateByIdAMI.class)
	int updateTemplateById(T entity);

	/**
	 * 按照主键更新更新或插入,自增或者序列id自动赋值给entity
	 *
	 * @param entity 待更新/插入的实体对象
	 * @return 如果是插入操作，返回true，如果是更新，返回false
	 */
	@AutoMapper(UpsertAMI.class)
	boolean upsert(T entity);

	/**
	 * 按照主键更新或插入，更新失败，会调用插入，属性为空的字段将不更新或者插入。自增或者序列id自动赋值给entity
	 *
	 * @param entity 待更新/插入的实体对象
	 * @return boolean
	 */

	@AutoMapper(UpsertByTemplateAMI.class)
	boolean upsertByTemplate(T entity);

	/**
	 * 根据主键删除对象，如果对象是复合主键，传入对象本生即可
	 *
	 * @param key 主键
	 * @return int
	 */
	@AutoMapper(DeleteByIdAMI.class)
	int deleteById(Object key);

	/**
	 * 根据主键获取对象，如果对象不存在，则会抛出一个Runtime异常
	 *
	 * @param key 主键
	 * @return T
	 */
	@AutoMapper(UniqueAMI.class)
	T unique(Object key);

	/**
	 * 根据主键获取对象，如果对象不存在，返回null
	 *
	 * @param key 主键
	 * @return T
	 */
	@AutoMapper(SingleAMI.class)
	T single(Object key);

	/**
	 * 根据一批主键查询
	 *
	 * @param key 主键集合
	 * @return List
	 */
	@AutoMapper(SelectByIdsAMI.class)
	List<T> selectByIds(List<?> key);

	/**
	 * 根据主键判断记录是否存在
	 *
	 * @param key 主键
	 * @return boolean
	 */
	default boolean exist(Object key) {
		return this.getSQLManager().exist(this.getTargetEntity(), key);
	}

	/**
	 * 根据主键获取对象，如果在事物中执行会添加数据库行级锁(select * from table where id = ? for update)，如果对象不存在，返回null
	 *
	 * @param key 主键
	 * @return T
	 */
	@AutoMapper(LockAMI.class)
	T lock(Object key);

	/**
	 * 返回实体对应的所有数据库记录
	 *
	 * @return List
	 */
	@AutoMapper(AllAMI.class)
	List<T> all();

	/**
	 * 返回实体在数据库里的总数
	 *
	 * @return long
	 */
	@AutoMapper(AllCountAMI.class)
	long allCount();

	/**
	 * 模板查询，返回符合模板得所有结果。beetlsql将取出非null值（日期类型排除在外），从数据库找出完全匹配的结果集
	 *
	 * @param entity 实体
	 * @return List
	 */
	@AutoMapper(TemplateAMI.class)
	List<T> template(T entity);

	/**
	 * 模板查询，返回一条结果,如果没有，返回null
	 *
	 * @param entity 实体
	 * @return T
	 */
	@AutoMapper(TemplateOneAMI.class)
	T templateOne(T entity);

	/**
	 * 符合模板得个数
	 *
	 * @param entity 实体
	 * @return long
	 */
	@AutoMapper(TemplateCountAMI.class)
	long templateCount(T entity);

	/**
	 * 执行一个jdbc sql模板查询
	 *
	 * @param sql  sql语句
	 * @param args sql参数
	 * @return List
	 */
	@AutoMapper(ExecuteAMI.class)
	List<T> execute(String sql, Object... args);

	/**
	 * 执行一个更新的jdbc sql
	 *
	 * @param sql  sql语句
	 * @param args sql参数
	 * @return int
	 */

	@AutoMapper(ExecuteUpdateAMI.class)
	int executeUpdate(String sql, Object... args);

	/**
	 * 获取 SQLManager
	 *
	 * @return SQLManager
	 */
	@AutoMapper(GetSQLManagerAMI.class)
	SQLManager getSQLManager();

	/**
	 * 返回一个Query对象
	 *
	 * @return Query
	 */
	@AutoMapper(QueryAMI.class)
	Query<T> createQuery();

	/**
	 * 返回一个LambdaQuery对象
	 *
	 * @return LambdaQuery
	 */
	@AutoMapper(LambdaQueryAMI.class)
	LambdaQuery<T> createLambdaQuery();

	/**
	 * 得到mapper的范型类
	 *
	 * @return Class
	 */
	@AutoMapper(GetTargetEntityAMI.class)
	Class<T> getTargetEntity();

	/**
	 * 根据主键，清空实体属性。复合主键暂不支持
	 *
	 * @param keys       主键集合
	 * @param properties 实体属性
	 * @return int
	 */
	default int clearFields(List<?> keys, LambdaQuery.Property<T, ?>... properties) {
		if (keys == null || keys.isEmpty()) {
			throw new BeetlSQLException(BeetlSQLException.ID_VALUE_ERROR, "主键不能为空");
		}
		Class<T> entityClass = this.getTargetEntity();
		SQLManager sqlManager = this.getSQLManager();
		LambdaQuery<T> query = sqlManager.lambdaQuery(entityClass);
		String tableName = sqlManager.getNc().getTableName(entityClass);
		if (tableName == null || "".equals(tableName)) {
			throw new BeetlSQLException(BeetlSQLException.TABLE_NOT_EXIST, "表名不存在");
		}
		Set<String> pkCols = sqlManager.getTableDesc(tableName).getIdNames();
		if (pkCols == null || pkCols.isEmpty()) {
			throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND, "主键列不存在");
		}
		//复合主键暂不支持
		query.andIn(pkCols.toArray()[0]+"", keys);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ");
		sql.append(tableName.toUpperCase()).append(" SET ");
		String updateCols = "";
		String[] columns = query.getColumnNames(properties);
		for (String column : columns) {
			String setCol = column + "=NULL";
			updateCols += "".equals(updateCols) ? setCol : "," + setCol;
		}
		sql.append(updateCols).append(" ");
		sql.append(query.getSql());
		SQLReady sqlReady = new SQLReady(sql.toString(), keys.toArray());
		return sqlManager.executeUpdate(sqlReady);
	}

}
