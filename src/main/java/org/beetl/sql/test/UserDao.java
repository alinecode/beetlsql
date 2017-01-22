package org.beetl.sql.test;


import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;


public interface UserDao extends BaseMapper<User> {
	public List queryUsers(PageQuery<User> query);
	
	public List<User> queryUsers(Map map);
	
	
	@Sql(value="select max(create_time) from user")
	public Timestamp getMax();
}
