package com.beetl.sql.firewall.test;

import com.beetl.sql.tenant.mapper.RewriteBaseMapper;
import org.beetl.sql.mapper.BaseMapper;

import java.util.List;

public interface MyCommonMapper extends BaseMapper<OrderLog> {
	List<OrderLog> select(String name);
}
