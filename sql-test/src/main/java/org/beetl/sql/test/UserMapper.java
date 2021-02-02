package org.beetl.sql.test;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.SqlResource;

import java.util.List;

@SqlResource("user")
public interface UserMapper extends BaseMapper<MyUser> {
	@Sql("select * from sys_user where id = 1")
	List<MyUser> query();

	Integer count(String name);
}
