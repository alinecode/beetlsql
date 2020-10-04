package org.beetl.sql.jmh.beetl;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.jmh.beetl.vo.BeetlSQLSysUser;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.mapper.annotation.Template;

@SqlResource("user")
public interface BeetlSQLUserMapper extends BaseMapper<BeetlSQLSysUser> {
    @Sql("select * from sys_user where id = ?")
    BeetlSQLSysUser selectById(Integer id);

    @Template("select * from sys_user where id = #{id}")
    BeetlSQLSysUser selectTemplateById(Integer id);
    /*user.md#userSelect */
    BeetlSQLSysUser userSelect(Integer id);


    PageResult<BeetlSQLSysUser> queryPage(String code, PageRequest request);
}
