package org.beetl.sql.core;

import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.entity.User;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 内置更新操作
 * @author xiandafu
 */
public class CoreUpdate extends BaseTest {
    @BeforeClass
    public static  void init(){
        initTable(testSqlFile);
    }

    @Test
    public void testUpdateById(){
        User user = sqlManager.single(User.class,1);
        user.setName("lijz-abc");
        sqlManager.updateById(user);
        User user1 = sqlManager.single(User.class,1);
        Assert.assertEquals(user1.getName(),user.getName());
    }


	@Table(name = "order")
	@Data
	public static class Order {
		@AutoID
		private Integer id;

		private String orderId;

		private String phone;

		private String flag;

		private String state;

		private String callback;

		private Date createTime;

		private Date updateTime;

	}


		@Test
    public void testUpdateByTemplate(){
        User template = new User();
        template.setId(1);
        template.setName("lijz-abc");
        sqlManager.updateTemplateById(template);
        User user1 = sqlManager.single(User.class,1);
        Assert.assertEquals(user1.getName(),template.getName());
        Assert.assertNotNull(user1.getDepartmentId());
    }

    @Test
    public void insert(){
        User user = new User();
        user.setId(4);
        user.setName("newName");
        user.setDepartmentId(1);
        user.setCreateDate(new Date());
        sqlManager.insert(user);
        Assert.assertNotNull(user.getId());
        User dbUser = sqlManager.single(User.class,user.getId());
        Assert.assertEquals(user.getName(),dbUser.getName());



    }

	@Test
	public void insertTemplate(){
		User user = new User();
		user.setId(15);
		user.setName("newName");
		user.setDepartmentId(1);
		sqlManager.insertTemplate(user);
		Assert.assertNotNull(user.getId());
		User dbUser = sqlManager.single(User.class,user.getId());
		Assert.assertEquals(user.getName(),dbUser.getName());
		Assert.assertNull(dbUser.getCreateDate());


		User user2 = new User();
		user2.setId(15);
		user2.setDepartmentId(2);
		user2.setCreateDate(new Date());
		sqlManager.updateTemplateById(user2);
	}


    @Test
    public void batchInsert(){
        long  count = sqlManager.allCount(User.class);

        User user = new User();
        user.setName("newName");
        user.setDepartmentId(1);
        user.setCreateDate(new Date());

        User user2 = new User();
        user2.setName("newName");
        user2.setDepartmentId(1);
        user2.setCreateDate(new Date());

        List list = Arrays.asList(user,user2);
		sqlManager.setBatchLogOneByOne(false);
        sqlManager.insertBatch(User.class,list);
		Assert.assertNotNull(user2.getId());
        long newCount = sqlManager.allCount(User.class);
        Assert.assertEquals(newCount,count+2);

    }

    @Test
    public void batchUpdate(){

        User user1 = sqlManager.unique(User.class,1);
        User user2 = sqlManager.unique(User.class,2);
        user1.setName("abc");
        user2.setName("abc");

        List list = Arrays.asList(user1,user2);
        sqlManager.updateByIdBatch(list);
        user1 = sqlManager.unique(User.class,1);
        Assert.assertEquals("abc",user1.getName());

    }


    @Test
    public void batchSQLBatchReady(){
        SQLBatchReady sqlBatchReady = new SQLBatchReady("update sys_user set name=? where id = ?",
                Arrays.asList(new Object[]{"abc",1},new Object[]{"abcd",2}));
		sqlManager.setBatchLogOneByOne(true);
        sqlManager.executeBatchUpdate(sqlBatchReady);
        User user = sqlManager.unique(User.class,1);
        User user2 = sqlManager.unique(User.class,2);
        Assert.assertEquals("abc",user.getName());
        Assert.assertEquals("abcd",user2.getName());
    }


	@Test
	public void batchTemplateUpdate(){
		{
			//内部分成俩组，验证输出了2条sql语句 setBatchLogOneByOne(false);
			User user1 = new User();
			user1.setName("a");
			user1.setId(1);

			User user2 = new User();
			user2.setName("b");
			user2.setId(2);

			User user3 = new User();
			user3.setName("c");
			user3.setDepartmentId(1);
			user3.setId(3);

			sqlManager.setInters(new DebugInterceptor[]{new DebugInterceptor()});
			List<User> users = Arrays.asList(user1,user2,user3);
			sqlManager.setBatchLogOneByOne(false);
			sqlManager.updateBatchTemplateById(User.class,users);
		}
		{
			//按照每条打印 setBatchLogOneByOne(true);
			String template = "update sys_user set name=#{name} where id = #{id}";
			User user1 = new User();
			user1.setName("a");
			user1.setId(1);

			User user2 = new User();
			user2.setName("b");
			user2.setId(2);
			sqlManager.setInters(new DebugInterceptor[]{new DebugInterceptor()});
			sqlManager.setBatchLogOneByOne(true);
			List<User> users = Arrays.asList(user1,user2);
			sqlManager.executeBatchTemplateUpdate(template,users);
		}


	}


	@Test
	public void batchTemplateUpdateById(){



	}





}
