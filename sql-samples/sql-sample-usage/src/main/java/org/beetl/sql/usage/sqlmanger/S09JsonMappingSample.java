package org.beetl.sql.usage.sqlmanger;

import lombok.Data;
import org.beetl.sql.annotation.entity.JsonMapper;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.mapping.join.JsonConfigMapper;
import org.beetl.sql.sample.SampleHelper;

import java.util.List;

/**
 * 演示使用json配置复杂映射
 */
public class S09JsonMappingSample {
    SQLManager sqlManager;

    public S09JsonMappingSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public static void main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S09JsonMappingSample sample = new S09JsonMappingSample(sqlManager);
        sample.selectUser();
        sample.selectDept();
        sample.selectUserByMdConfig();
        sample.selectUserByDynamicMdConfig();
        sample.selectUserByDynamicMdConfig2();
    }

    public void selectUser(){
        String sql = "select u.*,u.name as dept_name from sys_user u " +
                "left  join department d on u.department_id= d.id where u.id=? ";

        List<UserInfo> users = sqlManager.execute(new SQLReady(sql,1),UserInfo.class);
        UserInfo info= users.get(0);
        System.out.println(info.getDeptName());
    }

    public void selectDept(){
        String sql = "select d.id id,d.name name ,u.id u_id,u.name u_name " +
                " from department d join sys_user u on d.id=u.department_id  where d.id=?";
        Integer deptId = 1;
        SQLReady ready = new SQLReady(sql,new Object[]{deptId});
        List<DepartmentInfo> list = sqlManager.execute(ready,DepartmentInfo.class);
        System.out.println(list.toString());
    }

    /**
     * 映射配置放到文件里，类似mybatis
     */
    public void selectUserByMdConfig(){
        String sql = "select u.*,u.name as dept_name from sys_user u " +
                "left  join department d on u.department_id= d.id where u.id=? ";

        List<UserInfo2> users = sqlManager.execute(new SQLReady(sql,1),UserInfo2.class);
        UserInfo2 info= users.get(0);
        System.out.println(info.getDeptName());
    }

    /**
     * 映射配置放到sql文件里，通过调用脚本函数jsonMapping，传入配置id
     *
     * 参考 jsonConfig.md#userConfig
     */
    public void selectUserByDynamicMdConfig(){
        SqlId selectById = SqlId.of("jsonConfig","selectUser");
        UserInfo3 para = new UserInfo3();
        para.setId(1);
        List<UserInfo3> users = sqlManager.select(selectById,UserInfo3.class,para);
        UserInfo3 info= users.get(0);
        System.out.println(info.getDeptName());
    }

    /**
     * 另外一个较为复杂的例子,参考 jsonConfig.md#userDetailConfig
     */
    public void selectUserByDynamicMdConfig2(){
        SqlId selectById = SqlId.of("jsonConfig","selectUserDetail");
        UserInfo4 para = new UserInfo4();
        para.setId(1);
        List<UserInfo4> users = sqlManager.select(selectById,UserInfo4.class,para);
        UserInfo4 info= users.get(0);
        System.out.println(info.getDept().getName());
        System.out.println(info.getRoles());

    }



    /**
     * key为属性，value为列名
     */
    private static final String USER_MAPPING = "{'id':'id','name':'name','deptName':'dept_name'}";

    /**
     * 左链接，合并
     */
    private static final String DEPT_MAPPING = "{'id':'id','name':'name','users':{'id':'u_id','name':'u_name'}}";

    @Data
    @ResultProvider(JsonConfigMapper.class)
    @JsonMapper(DEPT_MAPPING)
    public static class DepartmentInfo {
        Integer id;
        String name;
        List<UserInfo> users;
    }

    @Data
    @ResultProvider(JsonConfigMapper.class)
    @JsonMapper(USER_MAPPING)
    public static class UserInfo {
        Integer id;
        String name;
        String deptName;
    }


    @Data
    @ResultProvider(JsonConfigMapper.class)
    //配置放到文件里
    @JsonMapper(resource="jsonConfig.userConfig")
    public static class UserInfo2 {
        Integer id;
        String name;
        String deptName;
    }

    /**
     * 配置来源于模板执行的时候的生成的变量,参考 jsonConfig.md#userConfig
     */
    @Data
    @ResultProvider(JsonConfigMapper.class)
    public static class UserInfo3 {
        Integer id;
        String name;
        String deptName;
    }

    /**
     * 一个更复杂例子,用户包含部门和多个角色，参考 jsonConfig.md#userDetailConfig
     */
    @Data
    @ResultProvider(JsonConfigMapper.class)
    public static class UserInfo4 {
        Integer id;
        String name;
        DepartmentInfo dept;
        List<RoleInfo> roles;
    }

    @Data
    public static class RoleInfo{
        private Integer id;
        private String name;
    }

}
