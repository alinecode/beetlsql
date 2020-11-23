package org.beetl.sql.saga.common;

import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.internal.SelectByIdsAMI;
import org.beetl.sql.mapper.internal.SingleAMI;
import org.beetl.sql.mapper.internal.UniqueAMI;
import org.beetl.sql.saga.common.ami.SagaDeleteByIdAMI;
import org.beetl.sql.saga.common.ami.SagaInsertAMI;
import org.beetl.sql.saga.common.ami.SagaUpdateByIdAMI;

import java.util.List;

/**
 * 类似BaseMapper，但提供了回滚方法逻辑
 * @param <T>
 */
public interface SagaMapper<T> {
	/** sega 改造的接口**/
	@AutoMapper(SagaInsertAMI.class)
	void insert(T entity);

	@AutoMapper(SagaUpdateByIdAMI.class)
	int updateById(T entity);

	@AutoMapper(SagaDeleteByIdAMI.class)
	int deleteById(Object key);


	/**  正常接口  **/
	@AutoMapper(SingleAMI.class)
	T single(Object key);

	@AutoMapper(UniqueAMI.class)
	T unique(Object key);

	@AutoMapper(SelectByIdsAMI.class)
	List<T> selectByIds(List<?> key);

}
