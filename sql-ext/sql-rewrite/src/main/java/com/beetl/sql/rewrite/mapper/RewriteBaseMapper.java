package com.beetl.sql.rewrite.mapper;

import com.beetl.sql.rewrite.annotation.DisableRewrite;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.internal.*;

import java.util.List;

/**
 * 同baseMapper，未来考虑重写某些AMI，比如unique，这样不需要sql解析就可以拼接出一个sql
 * @param <T>
 */
public interface RewriteBaseMapper<T> extends BaseMapper<T> {

	@AutoMapper(InsertAMI.class)
	@DisableRewrite //减少sql解析
	int insert(T entity);

	@AutoMapper(InsertTemplateAMI.class)
	@DisableRewrite //减少sql解析
	int insertTemplate(T entity);


	@AutoMapper(InsertBatchAMI.class)
	@DisableRewrite //减少sql解析
	void insertBatch(List<T> list);


}
