package org.beetl.sql.usage.mapper.dao;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.mapper.annotation.Template;
import org.beetl.sql.mapper.annotation.Update;
import org.beetl.sql.sample.entity.UserEntity;

import java.util.List;

/**
 * 演示更新操作
 */
@SqlResource("user")
public interface UserUpdateMapper extends BaseMapper<UserEntity> {

    @Sql("update sys_user set name=?    where   id = ?")
    @Update
    int updateName(String name,Integer id);


    @Template("update sys_user set name=#{name}  where id = #{myId} ")
    @Update
    int updateNameBySqlTemplate(String name,Integer myId);


    @Sql("delete from sys_user where name=?")
    @Update
    int deleteUser(String name);

    /**
     * join函数可以自动把数组或者集合类参数展开
     * @param ids
     * @return
     */
    @Template("delete from sys_user where id in (#{join(ids)})")
    @Update
    int deleteAllUser(List<Integer> ids);

	/**
	 * 自己定义一个
	 * @param users
	 * @return
	 */
    default int [] batchUpdateById(List<UserEntity> users){
        return this.getSQLManager().updateByIdBatch(users);
    }

    @Update
    int updateBySqlId(String name,Integer id);

}
