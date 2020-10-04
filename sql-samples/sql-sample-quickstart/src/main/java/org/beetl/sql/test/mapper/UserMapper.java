package org.beetl.sql.test.mapper;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.*;
import org.beetl.sql.sample.entity.DepartmentEntity;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.test.S2MappingSample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SqlResource("user") /*sql文件在user.md里*/
public interface UserMapper extends BaseMapper<UserEntity> {


    @Sql("select * from sys_user where id = ?")
    @Select
    UserEntity queryUserById(Integer id);

    @Sql("update sys_user set name=? where id = ?")
    @Update
    int updateName(String name,Integer id);

    @Template("select * from sys_user where id = #{id}")
    UserEntity getUserById(Integer id);

    @SpringData
    List<UserEntity> queryByNameOrderById(String name);

    /**
     * 可以定义一个default接口
     * @return
     */
     default  List<DepartmentEntity> findAllDepartment(){
        Map paras = new HashMap();
        paras.put("exlcudeId",1);
        List<DepartmentEntity> list = getSQLManager().execute("select * from department where id != #{exlcudeId}",DepartmentEntity.class,paras);
        return list;
    }


    /**
     * 调用sql文件user.md#select,方法名即markdown片段名字
     * @param name
     * @return
     */
     List<UserEntity> select(String name);


    /**
     *  SimpleJoinMappper 颜色
     * @return
     */
    @Sql("select u.id ,u.name  ,d.id `dept.id`,d.name `dept.name` " +
             " from sys_user u  left join department d on d.id=u.department_id")
     List<S2MappingSample.MyUserView> allUserView();

    /**
     * 翻页查询,调用user.md#pageQuery
     * @param deptId
     * @param pageRequest
     * @return
     */
    PageResult<UserEntity>  pageQuery(Integer deptId, PageRequest pageRequest);

    /**
     * 翻页查询,调用user.md#pageQuery2,结果集封装到到map
     * @param deptId
     * @param pageRequest
     * @return
     */
    PageResult<Map>  pageQuery2(Integer deptId, PageRequest pageRequest);


	@Sql("select * from sys_user where department_id = ?")
	PageResult<UserEntity> queryDeptById(Integer id,PageRequest pageRequest);


	@Template("select #{page()} from sys_user where department_id = #{id}")
	PageResult<UserEntity> queryTemplateDeptById(Integer id,PageRequest pageRequest);



}
