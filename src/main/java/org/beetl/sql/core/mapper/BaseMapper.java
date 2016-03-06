package org.beetl.sql.core.mapper;

import java.util.List;

import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;

/**
 * BaseMapper.
 *
 * @param <T>
 *            the generic type
 */
public interface BaseMapper<T> {

	/* insert */
	void insert(T entity);
	void insert(T entity,boolean assignKey);
	KeyHolder insertReturnKey(T entity);
	
	/*update*/
	int updateById(T entity);
	int updateTemplateById(T entity);
	
	/*delete*/
	int deleteById(Object key);

	/*select */
	
	T unique(Object key);
	
	List<T> all();
	List<T> all(int start,int size);
	long allCount();
	
	
	List<T> template(T entity);
	List<T> template(T entity,int start,int size);
	long templateCount(T entity);
	

	
	

}
