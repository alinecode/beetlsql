package org.beetl.sql.jmh.weed.mapper;


import org.beetl.sql.jmh.weed.model.WeedSQLSysUser;
import org.noear.weed.BaseMapper;
import org.noear.weed.annotation.Sql;
import org.noear.weed.xml.Namespace;

import java.util.List;

@Namespace("org.beetl.sql.jmh.weed.mapper")
public interface WeedSQLUserMapper extends BaseMapper<WeedSQLSysUser> {
    @Sql("select * from sys_user where id = ?")
    WeedSQLSysUser selectById2(Integer id);

    @Sql("select * from sys_user where id = @{id}")
    WeedSQLSysUser selectTemplateById(Integer id);

    WeedSQLSysUser userSelect(Integer id);

    List<WeedSQLSysUser> queryPage(String code, int start, int end);
}