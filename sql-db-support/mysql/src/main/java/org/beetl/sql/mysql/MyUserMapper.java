package org.beetl.sql.mysql;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.SqlResource;

import java.util.List;
@SqlResource("system.user")
public interface MyUserMapper  extends BaseMapper<MysqlUser> {
     @Sql("select * from user")
     List count();
     MysqlUser select();
}
