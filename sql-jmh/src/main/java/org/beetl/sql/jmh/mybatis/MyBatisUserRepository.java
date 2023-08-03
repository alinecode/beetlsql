package org.beetl.sql.jmh.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.beetl.sql.jmh.mybatis.vo.MyBatisSysCustomerView;
import org.beetl.sql.jmh.mybatis.vo.MyBatisSysUser;

import java.util.List;

public interface MyBatisUserRepository extends BaseMapper<MyBatisSysUser> {
    @Select("select * from sys_user where id = #{id}")
    public MyBatisSysUser selectEntityById(@Param("id") Integer id);
    public MyBatisSysUser selectUser(@Param("id") Integer id);

    public MyBatisSysCustomerView selectView(@Param("id") Integer id);
	@Select("select * from sys_user")
	public List<MyBatisSysUser> selectEntities();

}
