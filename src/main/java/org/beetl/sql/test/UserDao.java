package org.beetl.sql.test;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

public interface UserDao extends BaseMapper<User> {
	@SqlStatement(params="name,age,_st,_sz")
	public List<User> queryUser( String name, Integer age, int start,int size);
	public User findById(@Param("id") Integer id);
	public int getCount();
	public Integer setAge(@Param("id") Integer id,@Param("age") Integer age);
	@SqlStatement(params="_root,name")
	public void setUserStatus(Map paras,String name);
	public int[] setUserStatus(Map<String,Object>[] paras);
	public int[] setUserStatus(List<User> paras);
	public KeyHolder newUser(User user);
	public void queryNewUser(PageQuery query);
	public int queryNewUser$count();
	@SqlStatement(sqlReady="update user set age = ? where id = ? ")
	public void updateAge(int age,int id);
	@SqlStatement(sqlReady="select * from user  ")
	public List<User> selectAll();
}
