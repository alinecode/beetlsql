package org.beetl.sql.test;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.RowSize;
import org.beetl.sql.core.annotatoin.RowStart;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.mapper.BaseMapper;

public interface UserDao extends BaseMapper<User> {
	public List<User> queryUser(@Param("name") String name,@Param("age") Integer age,@RowStart int start,@RowSize int size);
	public User findById(@Param("id") Integer id);
	public Integer getCount();
	public Integer setAge(@Param("id") Integer id,@Param("age") Integer age);
	public void setUserStatus(Map paras);
	public KeyHolder newUser(User user);
}
