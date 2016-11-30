package org.beetl.sql.test;

import java.util.Map;

import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;


public interface UserDao extends BaseMapper<User> {
	@SqlStatement()
	public void queryUsers(PageQuery query);
}
