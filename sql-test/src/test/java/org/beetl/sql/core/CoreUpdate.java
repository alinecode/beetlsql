package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.entity.User;
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
		user.setId(5);
		user.setName("newName");
		user.setDepartmentId(1);
		sqlManager.insertTemplate(user);
		Assert.assertNotNull(user.getId());
		User dbUser = sqlManager.single(User.class,user.getId());
		Assert.assertEquals(user.getName(),dbUser.getName());
		Assert.assertNull(dbUser.getCreateDate());

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
    public void batchBatchUpdate(){

        /*只批量更新部分字段数据*/
        User user1 = new User();
        user1.setName("abc");
        user1.setId(1);

        User user2 = new User();
        user2.setDepartmentId(2);
        user2.setId(2);

        List list = Arrays.asList(user1,user2);
        sqlManager.updateBatchTemplateById(User.class,list);
        user1 = sqlManager.unique(User.class,1);
        Assert.assertEquals("abc",user1.getName());

    }





}
