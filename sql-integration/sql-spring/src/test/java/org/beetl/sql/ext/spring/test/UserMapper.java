package org.beetl.sql.ext.spring.test;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.SqlResource;

@SqlResource("user")
public interface UserMapper extends BaseMapper<UserInfo> {
	public UserInfo select();
}
