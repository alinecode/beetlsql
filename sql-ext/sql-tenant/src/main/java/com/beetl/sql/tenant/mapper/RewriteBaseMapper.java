package com.beetl.sql.tenant.mapper;

import com.beetl.sql.tenant.annotation.DisableRewrite;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
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
	void insert(T entity);

	@AutoMapper(InsertTemplateAMI.class)
	@DisableRewrite //减少sql解析
	void insertTemplate(T entity);


	@AutoMapper(InsertBatchAMI.class)
	@DisableRewrite //减少sql解析
	void insertBatch(List<T> list);


}
