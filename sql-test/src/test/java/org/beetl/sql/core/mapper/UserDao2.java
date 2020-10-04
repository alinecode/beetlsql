package org.beetl.sql.core.mapper;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.*;

import java.util.List;

/**
 * 参数命名
 */
public interface UserDao2 extends BaseMapper<User> {

    @Template("select * from sys_user where id=#{id}")
    public User queryByName(User user);

    @Template("select * from sys_user where id=#{id}")
    public User queryByName2(@Root User user);

    @Template("select * from sys_user where id=#{u.id}")
    public User queryByName3(@Param("u") User user);

    @Template("select * from sys_user where name=#{name} and age=#{u.age}")
    public User queryByName4(@Root User  root,@Param("u") User user);

    @Template("select * from sys_user where name=#{u1.name} and age=#{u2.age}")
    public User queryByName5(@Param("u1") User  root,@Param("u2") User user);

    @Template("select * from sys_user where id=#{u1.id}")
    public User queryByName6(@Param("u1") @Root User  root);

    @Template("select * from sys_user where id=#{id}")
    public User queryByName7(@Param("u1") @Root User  user,@Root User root);

}
