package org.beetl.sql.jmh.wood.mapper;


import org.beetl.sql.jmh.wood.model.WoodSQLSysUser;
import org.noear.wood.BaseMapper;
import org.noear.wood.annotation.Sql;
import org.noear.wood.xml.Namespace;

import java.util.List;

@Namespace("org.beetl.sql.jmh.wood.mapper")
public interface WoodSQLUserMapper extends BaseMapper<WoodSQLSysUser> {
    @Sql("select * from sys_user where id = ?")
    WoodSQLSysUser selectById2(Integer id);

    @Sql("select * from sys_user where id = @{id}")
    WoodSQLSysUser selectTemplateById(Integer id);

    WoodSQLSysUser userSelect(Integer id);

    List<WoodSQLSysUser> queryPage(String code, int start, int end);
}
