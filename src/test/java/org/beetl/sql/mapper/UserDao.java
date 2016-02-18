package org.beetl.sql.mapper;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.RowSize;
import org.beetl.sql.core.annotatoin.RowStart;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.annotatoin.SqlStatementType;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.pojo.User;


public interface UserDao extends BaseMapper<User> {
	public List<User> queryUser(@Param("name") String name,@Param("age") Integer age,@RowStart int start,@RowSize int size);
	public User findById(@Param("id") Integer id);
	public int getCount();
	public Integer setAge(@Param("id") Integer id,@Param("age") Integer age);
	public void setUserStatus(Map paras);
	public int[] setUserStatus(Map<String,Object>[] paras);
	public int[] setUserStatus(List<User> paras);
	@SqlStatement(type=SqlStatementType.INSERT)
	public KeyHolder newUser(User user);
}