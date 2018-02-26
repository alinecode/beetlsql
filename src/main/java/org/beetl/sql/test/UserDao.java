package org.beetl.sql.test;


import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

@SqlResource("wan.user")
public interface UserDao extends BaseMapper {
	
	List<Long> getIds();
	void getIds(PageQuery<User> query);
	void pageQuery(PageQuery<String> query);
	
	int getCount(String name);
	
	User getOneUser();
	
	List<Map<String,Object>> getIdNames();
	
	@Sql(value="select id from user")
	List<Long> getIds2();
	
	@Sql(value="select id,name from user where id=?")
	Map<String,Long> getUserInfo(Long id);

	
	List getUsers(int hi, User user);
	
	@Sql("select * from user where name=? ")
	PageQuery<User>  getUser4(int pageNumber,int pageSize,String name);
	void getUser5(PageQuery<User> query,String name);
	
	List<User> select();
	
	@SqlStatement(params="name,id")
	public int updateUser(String name,int id);
	
	int deleteByUserIds(List<Integer> userIds);
	
	public int updateUser(List<User> users);
	
	public KeyHolder addOne(User user);
	
}
