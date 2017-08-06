package org.beetl.sql.test.mysql.dao;

import java.util.List;

import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.test.mysql.entity.User;
/**
 * 内置方法测试
 * @author xiandafu
 *
 */
@SqlResource("user")
public interface UserDao extends BaseMapper<User>{
	public List<User> selectUsers(String names[]);
}
