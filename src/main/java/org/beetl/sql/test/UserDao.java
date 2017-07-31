package org.beetl.sql.test;


import java.util.List;

import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

@SqlResource("wan.user")
public interface UserDao extends BaseMapper<User> {
	
	List<Long> getIds();
	@Sql(value="select id from user")
	List<Long> getIds2();

	void getIds3(PageQuery<String> query);
	
	List getUsers(int hi, User user);
	
	@Sql("select * from user where name=? ")
	PageQuery<User> getUser4(int pageNumber,int pageSize,String name);
	void getUser5(PageQuery<User> query,String name);
	
	List queryUsers();
	
	@SqlStatement(params="hi,user")
	public int updateUser(String hi,User user);
	
	public int[] updateUser(List<User> users);
	
	public KeyHolder addOne(User user);
	
}
