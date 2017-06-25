package org.beetl.sql.test;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.engine.PageQuery;

public interface UserMyBaseMapper extends MyBaseMapper<User> {
	public PageQuery<User> getIds3(PageQuery<User> query);
	
}
