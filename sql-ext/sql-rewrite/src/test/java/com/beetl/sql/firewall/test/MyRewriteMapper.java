package com.beetl.sql.firewall.test;

import com.beetl.sql.rewrite.annotation.DisableRewrite;
import com.beetl.sql.rewrite.mapper.RewriteBaseMapper;
import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.internal.InsertAMI;

import java.util.List;

public interface MyRewriteMapper extends RewriteBaseMapper<OrderLog> {
	List<OrderLog> select(String name);
	@DisableRewrite
	List<OrderLog> select2(String name);

	@DisableRewrite //禁止sql重写
	@AutoMapper(InsertAMI.class)
	int insert(OrderLog entity);
}
