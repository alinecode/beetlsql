package org.beetl.sql.springboot.dynamic;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.springboot.UserInfo;

public interface DynamicUserInfoMapper extends BaseMapper<UserInfoInDs1> {
     /*另外一个SQLManager*/
     @Sql("select * from sys_user where id=?")
     UserInfoInDs2 queryById(Integer id);
}
