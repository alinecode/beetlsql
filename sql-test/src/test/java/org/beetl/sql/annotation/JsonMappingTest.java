package org.beetl.sql.annotation;

import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.JsonMapper;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;
import org.beetl.sql.core.mapping.join.JsonConfigMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class JsonMappingTest extends BaseTest {

    @BeforeClass
    public static void init() {
        initTable(testSqlFile);
    }

    @Test
    public void jsonConfigTest() {
        String sql = "select d.id id,d.name name ,u.id u_id,u.name u_name " +
                " from department d join sys_user u on d.id=u.department_id  where d.id=?";
        Integer deptId = 2;
        SQLReady ready = new SQLReady(sql,new Object[]{deptId});
        List<DepartmentInfo> list = sqlManager.execute(ready,DepartmentInfo.class);
        Assert.assertEquals(1,list.size());
        DepartmentInfo info =  list.get(0);
        Assert.assertEquals(2,info.getId().intValue());
        List<UserInfo> users =  info.getUsers();
        Assert.assertEquals(2,users.size());

    }

    @Test
    public void autoMapping(){
        String sql = "select u.id ,u.name  ,d.id `dept.id`,d.name `dept.name` " +
                " from sys_user u  left join department d on d.id=u.department_id";
        SQLReady ready = new SQLReady(sql);
        List<MyUserView> list = sqlManager.execute(ready,MyUserView.class);
        Assert.assertEquals(3,list.size());

    }


	@Test
	public void recursion(){
		{
			String sql = "select u.id ,u.name  , 1 `f.id` ,'test' `f.name`  " +
					" from sys_user u where u.id= ? ";
			SQLReady ready = new SQLReady(sql,1);
			List<UserFriendView> list = sqlManager.execute(ready,UserFriendView.class);
			Assert.assertEquals(1,list.size());

			UserFriendView view = list.get(0);

			Assert.assertEquals("test",view.getF().getName());
		}
		{
			String sql = "select u.id ,u.name  , 1 `f.id` ,'test' `f.name`  " +
					",2 `f.f.id` ,'test2' `f.f.name`"+
					" from sys_user u where u.id= ? ";
			SQLReady ready = new SQLReady(sql,1);
			List<UserFriendView> list = sqlManager.execute(ready,UserFriendView.class);
			Assert.assertEquals(1,list.size());

			UserFriendView view = list.get(0);
			UserFriendView friendView = view.getF().getF();
			Assert.assertEquals("test2",friendView.getName());

		}

	}


    @Data
    @ResultProvider(JsonConfigMapper.class)
    @JsonMapper(
            "{'id':'id','name':'name','users':{'id':'u_id','name':'u_name'}}")
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


    @Data
    @ResultProvider(AutoJsonMapper.class)
    public static class MyUserView {
        Integer id;
        String name;
        DepartmentEntity dept;
    }

	/**
	 * 嵌套循环
	 */
	@Data
	@ResultProvider(AutoJsonMapper.class)
	public static class UserFriendView {
		Integer id;
		String name;
		UserFriendView f;
	}

    @Table(name="department")
    @Data
    public static class DepartmentEntity {
        Integer id;
        String name;
    }



}
