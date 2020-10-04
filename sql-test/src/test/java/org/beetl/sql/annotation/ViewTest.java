package org.beetl.sql.annotation;

import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.annotation.entity.RowProvider;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.View;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.mapping.ResultSetMapper;
import org.beetl.sql.core.mapping.RowMapper;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewTest extends BaseTest {

    @BeforeClass
    public static void init() {
        initTable(testSqlFile);
    }

    @Test
    public void rowMapperTest() {
        User user = sqlManager.unique(User.class,1);
        TestObjectEntity testObjectEntity = sqlManager.unique(TestObjectEntity.class,1);
        String code = user.getId()+""+user.getName();
        Assert.assertEquals(code, testObjectEntity.getVerify());
    }

    @Test
    public void rowMapperTest2() {

        String sql = "select  u.*,d.name my_dept from sys_user u left join department d on u.department_id=d.id where u.id=?";
        TestUserWithDepartment user = sqlManager.execute(new SQLReady(sql,1),TestUserWithDepartment.class).get(0);
        String deptName = "部门1";
        Assert.assertEquals(deptName, user.getDepartName());
    }


    @Test
    public void rowMapperTest3() {

        String sql = "select  u.*,d.name my_dept from sys_user u left join department d on u.department_id=d.id where u.id=?";
        TestUserWithDepartment user = sqlManager.rowMapper(MyRowMapper3.class).execute(new SQLReady(sql,1),TestUserWithDepartment.class).get(0);
        String deptName = "部门1_abc";
        Assert.assertEquals(deptName, user.getDepartName());
    }



    @Test
    public void resultSetMap() {

        String sql = "select * from sys_user where id = ?";
        ResultSetObject user = sqlManager.execute(new SQLReady(sql,1),ResultSetObject.class).get(0);
        String deptName = "lijz";
        Assert.assertEquals(deptName, user.getMyName());
    }

    @Test
    public void resultSetMap2() {
        String sql = "select * from sys_user where id = ?";
        ResultSetObject2 user = sqlManager.resultSetMapper(MyResultSetMapper2.class).execute(new SQLReady(sql,1),ResultSetObject2.class).get(0);
        String deptName = "lijz";
        Assert.assertEquals(deptName, user.getMyName());
    }



    @Test
    public void view() {
        //默认查询
        MyViewEntity viewEntity = sqlManager.unique(MyViewEntity.class,1);
        Assert.assertNotNull(viewEntity.getAge());
        //按照simple视图查询
        viewEntity = sqlManager.viewType(Simple.class).unique(MyViewEntity.class,1);
        Assert.assertNull(viewEntity.getAge());
        // 按照all视图查询
        viewEntity =sqlManager.viewType(All.class).unique(MyViewEntity.class,1);
        Assert.assertNotNull(viewEntity.getAge());
    }





    @Table(name="sys_user")
    @RowProvider(MyRowMapper.class)
    @Data
    public static  class TestObjectEntity extends  User{
        String verify;
    }


    @RowProvider(MyRowMapper2.class)
    @Data
    public static  class TestUserWithDepartment extends  User{
        String departName;
    }


    public static class MyRowMapper  implements RowMapper<TestObjectEntity>{

        @Override
        public TestObjectEntity mapRow(ExecuteContext ctx, Object obj, ResultSet rs, int rowNum, Annotation config) throws SQLException {
            TestObjectEntity to = (TestObjectEntity)obj;
            //处理额外结果集
            String code = to.getId()+""+ to.getName();
            to.setVerify(code);
            return to;
        }
    }

    public static class MyRowMapper2  implements RowMapper<TestUserWithDepartment>{

        @Override
        public TestUserWithDepartment mapRow(ExecuteContext ctx, Object obj, ResultSet rs, int rowNum, Annotation config) throws SQLException {
            TestUserWithDepartment to = (TestUserWithDepartment)obj;
            String dept = rs.getString("my_dept");
            to.setDepartName(dept);
            return to;
        }
    }

    public static class MyRowMapper3  implements RowMapper<TestUserWithDepartment>{

        @Override
        public TestUserWithDepartment mapRow(ExecuteContext ctx, Object obj, ResultSet rs, int rowNum, Annotation config) throws SQLException {
            TestUserWithDepartment to = (TestUserWithDepartment)obj;
            String dept = rs.getString("my_dept");
            to.setDepartName(dept+"_abc");
            return to;
        }
    }

    @Data
    @Table(name="sys_user")
    public static class MyViewEntity{
        @View({Simple.class,All.class})
        Integer id;
        @View({Simple.class,All.class})
        String name;
        @View({All.class})
        Integer age;
    }

    public static interface Simple{}
    public static interface All{}


    @Data
    @ResultProvider(MyResultSetMapper.class)
    public static class ResultSetObject{
        private Integer myId;
        private String myName;

    }

    public static class MyResultSetMapper  implements ResultSetMapper<ResultSetObject>{
        @Override
        public List<ResultSetObject> mapping(ExecuteContext ctx, Class target, ResultSet resultSet, Annotation config) throws SQLException {

            List<ResultSetObject> list = new ArrayList<>();
            while(resultSet.next()){
                ResultSetObject obj = new ResultSetObject();
                obj.setMyId(resultSet.getInt("id"));
                obj.setMyName(resultSet.getString("name"));
                list.add(obj);
            }

            return list;
        }
    }


    @Data
    public static class ResultSetObject2{
        private Integer myId;
        private String myName;

    }

    public static class MyResultSetMapper2  implements ResultSetMapper<ResultSetObject2>{
        @Override
        public List<ResultSetObject2> mapping(ExecuteContext ctx, Class target, ResultSet resultSet, Annotation config) throws SQLException {

            List<ResultSetObject2> list = new ArrayList<>();
            while(resultSet.next()){
                ResultSetObject2 obj = new ResultSetObject2();
                obj.setMyId(resultSet.getInt("id"));
                obj.setMyName(resultSet.getString("name"));
                list.add(obj);
            }

            return list;
        }
    }


}