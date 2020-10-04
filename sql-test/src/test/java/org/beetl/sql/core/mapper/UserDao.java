package org.beetl.sql.core.mapper;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.*;

import java.util.List;
@SqlResource("mapper.userMapper")
public interface UserDao  extends BaseMapper<User> {

    @Template("select * from sys_user where id=#{id}")
    public User queryTemplateById(Integer id);

    @Sql("select * from sys_user where id=?")
    public User querySqlById(Integer id);


    @Template("update sys_user set name=#{name}  where id=#{id}")
    @Update
    public int updateName(Integer id,String name);

    @SqlProvider(provider=UserSqlProvider.class)
    public User queryProviderById(Integer id);


    @SqlTemplateProvider(provider=UserSqlProvider.class)
    public User queryTemplateProviderById(Integer id);

    @SpringData
    public User getById(Integer id);

    @SpringData
    public List<User> getByName(String name);

    @SpringData
    public List<User> getByAgeOrNameOrderByIdAsc(Integer age,String name);


    PageResult<User> page(String name, Integer age, PageRequest whatEver);

    public default int count(Integer age){
       long count =  createLambdaQuery().andEq(User::getAge,age).count();
       return (int)count;
    }


}
