package org.beetl.sql.springboot.datasource2.db1;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.springboot.UserInfo;

@SqlResource("user")
public interface Db1Mapper extends BaseMapper<UserInfo> {
	public UserInfo select();
}
