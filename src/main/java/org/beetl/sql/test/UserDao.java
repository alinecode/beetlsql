package org.beetl.sql.test;


import java.util.List;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

@SqlResource("wan.user")
public interface UserDao extends BaseMapper<User> {
	
	List<Long> getIds();
	@Sql(value="select id from user")
	List<Long> getIds2();

	void getIds3(PageQuery<String> query);
	
	List getUsers(User user,@Param("hi") int test);
	
	@Sql("select * from axeac_datasource order by updatedtm desc")
	PageQuery<User> getUser4(int pageNumber,int pageSize);
	PageQuery<User> getUser5();
	
	List queryUsers();
	
}
