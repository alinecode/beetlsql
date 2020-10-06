package org.beetl.sql.usage.sqlmanger;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.DefaultPageResult;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchOne;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.DepartmentEntity;
import org.beetl.sql.sample.entity.UserEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.beetl.sql.sample.SampleHelper.printPageResult;

/**
 * 使用sqlManager查询
 * @author xiandafu
 */
public class S06SelectSample {
    SQLManager sqlManager;

    public S06SelectSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public static void main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S06SelectSample sample = new S06SelectSample(sqlManager);
        sample.selectById();
        sample.userInfo();
        sample.all();
        sample.template();
        sample.execute();
        sample.executePage();
        sample.executeTemplate();
        sample.mapping();
        sample.fetch();

        //复杂的sql最好维护在sql文件里，而不是java代码里
        sample.resourceId();
        sample.pageResourceId();
        sample.groupPageResourceId();
        sample.includeOtherSql();
        sample.likeAndIn();


    }

    /**
     * 最常用的情况
     */
    public void selectById() {
        //unique方法只查询一条，如果不存在或者多余一条，都会抛错
        UserEntity userEntity = sqlManager.unique(UserEntity.class, 1);
        //返回一条，如果没有，则返回空，如果多余一条，返回第一条
        UserEntity userEntity2 = sqlManager.single(UserEntity.class, 1);
        //判断是否存在
        boolean hasData = sqlManager.exist(UserEntity.class, 1);
        // 根据主键查询多个数据，注意，有些数据库支持in表达式，会设定最多参数个数，beesql并未考虑到这点，程序需要自己考虑
        List<UserEntity> list = sqlManager.selectByIds(UserEntity.class, Arrays.asList(1, 2));
    }

    /**
     * 任何对象都可以是sqlManager的操作对象
     */
    public void userInfo() {
        UserInfo userEntity = sqlManager.unique(UserInfo.class, 1);
        UserInfoDetail userInfoDetail = sqlManager.unique(UserInfoDetail.class, 1);
    }

    /**
     * 查询表所有数据
     */
    public void all() {
        long count = sqlManager.allCount(UserEntity.class);
        if (count < 1000) {
            List<UserEntity> list = sqlManager.all(UserEntity.class);
        }

    }

    /**
     * template方法，完全匹配
     */
    public void template() {
        UserEntity template = new UserEntity();
        template.setName("abc");
        long count = sqlManager.templateCount(template);
        if (count < 1000) {
            List<UserEntity> list = sqlManager.template(template);
        }

        /*取第一条*/
        UserEntity entity = sqlManager.templateOne(template);


    }

    /**
     * 直接使用sql
     */
    public void execute() {
        List<UserEntity> list = sqlManager.execute(new SQLReady("select * from sys_user where department_id=?", 1), UserEntity.class);
        Long count = sqlManager.execute(new SQLReady("select count(1) from sys_user where department_id=?", 1), Long.class).get(0);
        Integer count2 = sqlManager.execute(new SQLReady("select count(1) from sys_user where department_id=?", 1), Integer.class).get(0);
        // 映射成Map 也行，但是Map不太好维护，不适合跨层，跨模块传递
        // map的key 命名也遵循NameConversion规则
        List<Map> listMap = sqlManager.execute(new SQLReady("select * from sys_user where department_id=?", 1), Map.class);
        System.out.println(listMap.get(0).get("departmentId"));


    }


    public void executePage() {
        SQLReady ready = new SQLReady("select * from sys_user where department_id=?", 1);
        PageRequest pageRequest = DefaultPageRequest.of(1,10);
        PageResult pageResult = sqlManager.execute(ready,UserEntity.class,pageRequest);
        SampleHelper.printPageResult((DefaultPageResult)pageResult);

    }


    /**
     * 查询结果可以映射到任何对象，以及Map
     * 更负载的映射实例可以参考代码SelectMappingSample
     *
     */
    public void mapping() {
        String sql = "select u.*,d.name department_name from sys_user u left join department d on u.department_id = d.id where u.id=?";
        UserView userView = sqlManager.execute(new SQLReady(sql, 1), UserView.class).get(0);
        System.out.println(userView.getDepartmentName());
        // 映射成Map 也行，但是Map不太好维护，不适合跨层，跨模块传递
        // map的key 命名也遵循NameConversion规则
        Map map = sqlManager.execute(new SQLReady(sql, 1), Map.class).get(0);
        System.out.println(map.get("departmentName"));

        //TailBean表示把无法匹配的列到TailBean的Map里
        UserView2 userView2 = sqlManager.execute(new SQLReady(sql, 1), UserView2.class).get(0);
        System.out.println(userView2.getId()+" :"+userView2.get("departmentName"));


    }

    public void executeTemplate(){
        String template = "select * from sys_user where department_id=#{departmentId}";
        UserEntity paras = new UserEntity();
        paras.setDepartmentId(1);
        List<UserEntity> list = sqlManager.execute(template,UserEntity.class,paras);
        //或者使用Map作为参数
        Map map = new HashMap();
        map.put("departmentId",1);
        list = sqlManager.execute(template,UserEntity.class,map);
        //映射结果也设定Map
        List<Map> listMap = sqlManager.execute(template,Map.class,map);
        System.out.println(listMap.get(0).get("id"));



    }

    /**
     * @fetch注解可以在查询完毕后，再自动查询
     */
    public void fetch() {
        MyUser myUser = sqlManager.unique(MyUser.class,1);
        System.out.println(myUser.getDept().getName());

        //fetch会合并查询
        List<MyUser> list = sqlManager.execute(new SQLReady("select * from sys_user where department_id=? or department_id =?", 1,2), MyUser.class);
        System.out.println(list.get(0).getDept().getName());

    }

    /**
     * 复杂的sql最好维护在sql文件里
     */
    public void resourceId(){
        //指向selectSample.md文件的selectByCondition片段
        SqlId sqlId = SqlId.of("selectSample","selectByCondition");
        Map map = new HashMap();
        map.put("name","li");
        List<UserEntity> list = sqlManager.select(sqlId,UserEntity.class,map);
        System.out.println(list.size());

        SqlId countSqlId = SqlId.of("selectSample","count");
        int count = sqlManager.intValue(countSqlId,new HashMap());
        long countLong = sqlManager.longValue(countSqlId,new HashMap());
        System.out.println(count+","+countLong);

        SqlId selectById = SqlId.of("selectSample","selectUserById");
        UserEntity para = new UserEntity();
        para.setId(1);
        UserEntity entity = sqlManager.selectUnique(selectById,para,UserEntity.class);

    }

    /**
     * beetlsql 能根据sql模板语句自动转化为求总数sql和翻页查询sql
     */
    public void pageResourceId(){

        SqlId selectById = SqlId.of("selectSample","pageQuery");
        //DefaultPageRequest是内置生成PageRequest内，你可以定制
        PageRequest pageRequest = DefaultPageRequest.of(1,20);
        PageResult pageResult = sqlManager.pageQuery(selectById,UserEntity.class,new HashMap(),pageRequest);
        printPageResult((DefaultPageResult)pageResult);


    }

    /**
     * 对于sql语句的结果是group by，有点特殊，需要转成子查询
     */
    public void groupPageResourceId(){

        SqlId selectById = SqlId.of("selectSample","groupByTest");
        //DefaultPageRequest是内置生成PageRequest内，你可以定制
        PageRequest pageRequest = DefaultPageRequest.of(1,20);
        PageResult pageResult = sqlManager.pageQuery(selectById,GroupCount.class,new HashMap(),pageRequest);
        printPageResult((DefaultPageResult)pageResult);

    }

    public void includeOtherSql(){
        SqlId includeTestId = SqlId.of("selectSample","includeTest");
        Map map = new HashMap();
        map.put("id",1);
        UserEntity user = sqlManager.selectUnique(includeTestId,map,UserEntity.class);


        SqlId globalIncludeTestId = SqlId.of("selectSample","globalIncludeTest");
         map = new HashMap();
        map.put("id",1);
         user = sqlManager.selectUnique(includeTestId,map,UserEntity.class);

    }

    public void likeAndIn(){
        SqlId likeSampleId = SqlId.of("selectSample","likeAndIn");
        String name = "li";
        Map map = new HashMap();
        map.put("ids",Arrays.asList(1,2,3));
        map.put("name","%"+name+"%");
        map.put("order","id asc");
        sqlManager.select(likeSampleId,UserEntity.class,map);


    }



    @Table(name = "sys_user")
    @Data
    public static class UserInfo {
        @AutoID
        private Integer id;
        private String name;

    }

    @Table(name = "sys_user")
    @Data
    public static class UserInfoDetail {
        @AssignID
        private Integer id;
        private String name;
        private Integer departmentId;

    }


    @Data
    public static class UserView {
        @AssignID
        private Integer id;
        private String name;
        private Integer departmentId;
        private String departmentName;

    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UserView2 extends TailBean {
        @AutoID
        private Integer id;
        private String name;

    }


    @Data
    @Fetch
    @Table(name="sys_user")
    public static class MyUser  {
        @AutoID
        private Integer id;
        private String name;
        private Integer departmentId;
        @FetchOne("departmentId")
        private DepartmentEntity dept;

    }

    @Data
    public static class GroupCount{
        String name;
        Integer count;
    }

}
