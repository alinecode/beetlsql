package org.beetl.sql.springboot.threadlocal;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;

public interface UserInfoMapper extends BaseMapper<UserInfo> {
     /*另外一个SQLManager*/
     @Sql("select * from sys_user where id=?")
     UserInfo queryById(Integer id);
}
