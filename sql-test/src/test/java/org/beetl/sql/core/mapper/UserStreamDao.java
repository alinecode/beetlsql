package org.beetl.sql.core.mapper;

import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.mapper.annotation.Template;

@SqlResource("user")
public interface UserStreamDao  extends BaseMapper<User> {
	@Sql("select * from sys_user where age!=?")
	StreamData queryBySql(Integer age);

	@Template("select * from sys_user where age!=#{age}")
	StreamData queryByTemplate(Integer age);

	StreamData streamTest();
}
