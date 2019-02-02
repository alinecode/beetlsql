package org.beetl.sql.test;


import java.sql.Timestamp;

import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;


public interface UserDao extends BaseMapper<User> {
	public void queryUsers(PageQuery<User> query);
	
	@Sql(value="select max(create_time) from user")
	public Timestamp getMax();
}
