package org.beetl.sql.test;


import lombok.Data;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.mapping.RowMapper;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;
import org.beetl.sql.core.mapping.join.JsonConfigMapper;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.DepartmentEntity;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.test.mapper.UserMapper;

import java.lang.annotation.*;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 默认情况，beetlsql从数据库中查询的数据，会与需要映射的任何java类(POJO）取交集，进行映射，但也有别的办法控制如何映射
 * 演示如何将数据库查询结果映射到java对象上
 *
 * @author xiandafu
 */

public class S2MappingSample {

    SQLManager sqlManager;
    UserMapper mapper =null;

    public S2MappingSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
        mapper = sqlManager.getMapper(UserMapper.class);
    }

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S2MappingSample mappingSample = new S2MappingSample(sqlManager);
//        mappingSample.column();
//        mappingSample.toMap();
//        mappingSample.view();
//        mappingSample.mappingProvider();
        mappingSample.jsonConfig();
//        mappingSample.autoMapping();
//        mappingSample.myAttributeAnnotation();
    }

    /**
     * 使用@Column注解，或者按照NameConversion来自动映射
     */
    public void column() {
        MyUser user = sqlManager.unique(MyUser.class, 1);

    }

    /**
     * 可以把查询结果转化成Map,在java中，注意，滥用map作为也业务对象是非常糟糕设计
     */
    public void toMap() {
        SQLReady sqlReady = new SQLReady("select id,name from sys_user");
        List<Map> list = sqlManager.execute(sqlReady, Map.class);

    }


    /**
     *
     */
    public void view() {
        //映射所有列
        TestUser user = sqlManager.unique(TestUser.class, 1);

        //映射只有一个KeyInfo标注的属性，本例子中department属性不在查询结果范围里
        TestUser keyInfo = sqlManager.viewType(TestUser.KeyInfo.class).unique(TestUser.class, 1);
    }

    /**
     * 使用额外的映射类来映射
     */
    public void mappingProvider() {
        //运行时刻指定一个映射类
        TestUser testUser = sqlManager.rowMapper(MyRowMapper.class).unique(TestUser2.class, 1);
        //使用@RowProvider注解为类指定一个Mapper，这个更常用
		TestUser2 testUser2 = sqlManager.unique(TestUser2.class, 1);
    }



    /**
     * 使用json 配置来映射，类似mybatis的xml配置
     */
    public void jsonConfig() {
        String sql = "select d.id id,d.name name ,u.id u_id,u.name u_name " +
                " from department d  left join sys_user u on d.id=u.department_id  where d.id=?";
        Integer deptId = 2;
        SQLReady ready = new SQLReady(sql,new Object[]{deptId});
        List<DepartmentInfo> list = sqlManager.execute(ready,DepartmentInfo.class);
        System.out.println(list.toString());

    }


    /**
     * 使用json 配置来映射，类似mybatis的xml配置
     */
    public void autoMapping() {
        List<MyUserView> list = mapper.allUserView();
        System.out.println(list);

    }

    /**
     * 自定义一个属性注解Base64,用于编码和解码属性字段
     */
    public void myAttributeAnnotation(){

        UserData userData = new UserData();
        userData.setName("123456");
        sqlManager.insert(userData);
        UserData data = sqlManager.unique(UserData.class,userData.getId());
        System.out.println("user name "+data.getName());

        UserEntity entity = sqlManager.unique(UserEntity.class,userData.getId());
        System.out.println("db value "+entity.getName());

    }



    /**
     * 演示使用Column 注解映射java属性与表列名，
     */
    @Data
    @Table(name="sys_user")
    public static class MyUser {
        @Column("id")
        @AutoID
        Integer myId;
        @Column("name")
        String myName;
    }


    @Data
    @Table(name="sys_user")
    public static class TestUser {
        public static interface KeyInfo {
        }

        @Column("id")
        @AutoID
        @View(KeyInfo.class)
        Integer myId;

        @Column("name")
        @View(KeyInfo.class)
        String myName;

        Integer departmentId;
    }


    @RowProvider(MyRowMapper.class)
    public static class TestUser2 extends TestUser {

    }

    /**
     * 使用json配置来映射,如果映射配置过长，建议放到文件中，使用resource说明配置路径
     *
     */
    @Data
    @ResultProvider(JsonConfigMapper.class)
//    @JsonMapper(
//            "{'id':'id','name':'name','users':{'id':'u_id','name':'u_name'}}")
    @org.beetl.sql.annotation.entity.JsonMapper(resource ="user.departmentJsonMapping")
    public static class DepartmentInfo {
        Integer id;
        String name;
        List<UserInfo> users;
    }

    @Data
    public static class UserInfo {
        Integer id;
        String name;
    }


    /**
     * 如果数据库查询的结果与类定义一致，也可以使用AutoJsonMapper
     */
    @Data
    @ResultProvider(AutoJsonMapper.class)
    public static class MyUserView {
        Integer id;
        String name;
        DepartmentEntity dept;
    }



    public static class MyRowMapper implements RowMapper<TestUser> {

        @Override
        public TestUser mapRow(ExecuteContext ctx, Object obj, ResultSet rs, int rowNum, Annotation config) throws SQLException {
            TestUser testUser = (TestUser) obj;
            testUser.setMyName(testUser.getMyName() + "-" + System.currentTimeMillis());
            return testUser;

        }
    }




    @Table(name="sys_user")
    @Data
    public static class UserData{
        @AutoID
        Integer id;
        @Base64
        String name;

    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.METHOD, ElementType.FIELD})
    @Builder(Base64Convert.class)
    public static @interface Base64 {

    }


    /**
     * 自定义一个注解，实现把属性字段加密存入数据库，取出的时候解密
     */
    public static class Base64Convert  implements AttributeConvert {
        Charset utf8  = Charset.forName("UTF-8");

        @Override
        public  Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {

            String value= (String) BeanKit.getBeanProperty(pojo,name);
            byte[] bs = java.util.Base64.getEncoder().encode(value.getBytes(utf8));
            return new String(bs,utf8);

        }
        @Override
        public  Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
            String value  = rs.getString(index);
            return new String(java.util.Base64.getDecoder().decode(value),utf8);
        }
    }


}
