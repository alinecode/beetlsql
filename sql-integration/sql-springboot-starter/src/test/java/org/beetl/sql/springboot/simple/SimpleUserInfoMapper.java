package org.beetl.sql.springboot.simple;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.springboot.UserInfo;

@SqlResource("user")
public interface SimpleUserInfoMapper extends BaseMapper<UserInfo> {
	public UserInfo select();
}
