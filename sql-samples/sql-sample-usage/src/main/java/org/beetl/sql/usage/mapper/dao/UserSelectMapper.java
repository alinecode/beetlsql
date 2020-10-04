package org.beetl.sql.usage.mapper.dao;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.*;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.usage.mapper.S01MapperSelectSample;

import java.util.List;

/**
 * 演示查询操作
 */
/*md文件名*/
@SqlResource("user")
public interface UserSelectMapper extends BaseMapper<UserEntity> {

    @Sql("select * from sys_user where name=? ")
    List<UserEntity> select(String name);

    /*如果返回单个实体，则beetlsql会从结果集取出第一个*/
    @Sql("select * from sys_user where id=? ")
    UserEntity selectSingleOne(Integer id );

    @Template("select * from sys_user where name=#{name} ")
    List<UserEntity> select2(String name);

    @SpringData
    List<UserEntity> queryByNameAndDepartmentId(String name,Integer deptId);

    default UserEntity selectById(Integer id ){
        UserEntity userEntity = this.getSQLManager().unique(UserEntity.class,id);
        return userEntity;
    }

    @Sql("select name,department_id deptId from sys_user  ")
    List<S01MapperSelectSample.UserInfo> anyPojo();


    @SqlProvider(provider= S01MapperSelectSample.SelectUserProvider.class)
    List<UserEntity> queryUserByCondition(String name);

    @SqlTemplateProvider(provider= S01MapperSelectSample.SelectUserProvider.class)
    List<UserEntity> queryUserByTemplateCondition(String name);

    /*无注解标识的都是查询md文件，*/
    List<UserEntity>  selectByUserName(String name);



}
