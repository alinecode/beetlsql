package com.beetl.sql.firewall.test;

import com.beetl.sql.rewrite.annotation.DisableRewrite;
import com.beetl.sql.rewrite.mapper.RewriteBaseMapper;

import java.util.List;

public interface MyRewriteMapper extends RewriteBaseMapper<OrderLog> {
	List<OrderLog> select(String name);
	@DisableRewrite
	List<OrderLog> select2(String name);
}
