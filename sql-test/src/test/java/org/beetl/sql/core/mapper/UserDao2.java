package org.beetl.sql.core.mapper;

import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.BatchUpdate;
import org.beetl.sql.mapper.annotation.Param;
import org.beetl.sql.mapper.annotation.Root;
import org.beetl.sql.mapper.annotation.Template;

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


	@Template("update sys_user set name=#{name} where id = #{id}")
	@BatchUpdate
	public int[] batchTemplateUpdate(List<User> pars);

	/**
	 * 限制Mapper的Sql或者Template注解的sql语句长度，以增强维护性
	 * @param user
	 * @return
	 */
	@Template("select * from sys_user where id=#{id} and test test test test test test "
			+ " and test test test test test test "
			+ " and test test test test test test "
			+ " and test test test test test test "
			+ " and test test test test test test "
			+ " and test test test test test test "
			+ " and test test test test test test ")
	public User queryByName8( User  user);

}
