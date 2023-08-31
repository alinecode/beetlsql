package com.beetl.sql.firewall.test;

import com.beetl.sql.tenant.annotation.DisableRewrite;
import com.beetl.sql.tenant.mapper.RewriteBaseMapper;

import java.util.List;

public interface MyRewriteMapper extends RewriteBaseMapper<OrderLog> {
	List<OrderLog> select(String name);
	@DisableRewrite
	List<OrderLog> select2(String name);
}
