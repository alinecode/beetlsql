package org.beetl.sql.core.query.interfacer;


import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author GavinKing
 */
public interface QueryExecuteI<M> {

	/***
	 * 指定字段查询，不传时查询所有
	 * @param columns
	 * @return 查询结果
	 */
	List<M> select(String... columns);

	List<M> select();

	/**
	 * 查询简单字段
	 * @return
	 */
	List<M> selectSimple();

	/**
	 * 查询，并映射到指定类上
	 *
	 * @param retType
	 * @return
	 */
	<K> List<K> select(Class<K> retType, String... columns);

	/**
	 * 查询结果集，结果集每一条并映射到map
	 *
	 * @return
	 */
	List<Map> mapSelect(String... columns);

	/**
	 * 查询一条记录，映射到Map
	 *
	 * @return
	 */
	Map mapSingle(String... columns);

	/**
	 * 查询出一条，如果没有，返回null
	 *
	 * @return
	 */
	<M> M single(String... columns);

	/**
	 * 查询一条，如果没有，返回null，返回所有列，包括blob/clob，如果想避免返回这种字段，可以使用
	 * {@code single(String... )} 或者 {@code singleSimple()}
	 * @param <M>
	 * @return
	 */
	default <M> M single(){
		return single(new String[0]);
	}

	/***
	 * 查询简单字段
	 * @param <M>
	 * @return
	 */
	<M> M singleSimple();

	/**
	 * 查询一条，如果没有或者有多条，抛异常
	 *
	 * @return
	 */
	<M> M unique(String... cols);


	/**
	 * 查询一条，如果没有或者有多条，抛异常,返回所有列，包括blob/clob，如果想避免返回这种字段，可以使用
	 * {@code unique(String... )} 或者 {@code uniqueSimple()}
	 *
	 * @return
	 */
	default <M> M unique(){
		return unique(new String[0]);
	}

	/**
	 * 查询一条，如果没有或者有多条，抛异常
	 *
	 * @return
	 */
	<M> M uniqueSimple();

	/***
	 * 全部更新，包括更新null值
	 * @param t,任意对象，或者Map
	 * @return 影响的行数
	 */
	int update(Object t);

	/***
	 * 有选择的更新
	 * @param t 任意对象或者Map
	 * @return 影响的行数
	 */
	int updateSelective(Object t);


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
	long count();

	PageResult<M> pageSimple(PageRequest pageRequest);

	PageResult<M> page(PageRequest pageRequest, String... columns);

	<K> PageResult<K> page(PageRequest pageRequest, Class<K> retType, String... columns);

	PageResult<Map> mapPage(PageRequest pageRequest, String... columns);

	/**
	 * 指定字段查询分页查询，不传入字段查询所有 SELECT *
	 *
	 * @param columns
	 * @return
	 */
	PageResult<M> page(long pageNumber, long pageSize, String... columns);

	/**
	 * 简单字段分页查询
	 *
	 * @return
	 */
	PageResult<M> pageSimple(long pageNumber, long pageSize);

	/**
	 * 分页查询，并映射到指定类上
	 *
	 * @param retType
	 * @return
	 */
	<K> PageResult<K> page(long pageNumber, long pageSize, Class<K> retType, String... columns);

	/**
	 * 分页查询结果集，结果集每一条并映射到map
	 *
	 * @return
	 */
	PageResult<Map> mapPage(long pageNumber, long pageSize, String... columns);
}
