package org.beetl.sql.test;


import java.util.List;

import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;


public interface UserDao extends BaseMapper<User> {
	
	List<Long> getIds();
	@Sql(value="select id from user")
	List<Long> getIds2();

	void getIds3(PageQuery<String> query);
	
	List getUsers();
	
}
