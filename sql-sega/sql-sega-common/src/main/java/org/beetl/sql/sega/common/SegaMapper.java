package org.beetl.sql.sega.common;

import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.internal.*;
import org.beetl.sql.sega.common.ami.SegaDeleteByIdAMI;
import org.beetl.sql.sega.common.ami.SegaInsertAMI;
import org.beetl.sql.sega.common.ami.SegaUpdateByIdAMI;

import java.util.List;

/**
 * 类似BaseMapper，但提供了回滚方法逻辑
 * @param <T>
 */
public interface SegaMapper<T> {
	/** sega 改造的接口**/
	@AutoMapper(SegaInsertAMI.class)
	void insert(T entity);

	@AutoMapper(SegaUpdateByIdAMI.class)
	int updateById(T entity);

	@AutoMapper(SegaDeleteByIdAMI.class)
	int deleteById(Object key);


	/**  正常接口  **/
	@AutoMapper(SingleAMI.class)
	T single(Object key);

	@AutoMapper(UniqueAMI.class)
	T unique(Object key);

	@AutoMapper(SelectByIdsAMI.class)
	List<T> selectByIds(List<?> key);

}
