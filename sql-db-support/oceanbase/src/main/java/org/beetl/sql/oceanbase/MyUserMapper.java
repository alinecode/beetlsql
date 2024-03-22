package org.beetl.sql.oceanbase;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.SqlResource;

import java.util.List;
@SqlResource("system.user")
public interface MyUserMapper  extends BaseMapper<OceanBaseUser> {
     @Sql("select * from my_user")
     List count();
     OceanBaseUser select();
}
