package org.beetl.sql.core;

import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试插入操作,更复杂的id操作，参考IdTest
 */
public class CoreInsert extends BaseTest {
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }

    @Test
    public void testAuto(){
		User1 user1 = new User1();
		user1.setName("ok1");
		sqlManager.insert(user1);
		Assert.assertNotNull(user1.getId());
		//赋值
		int id = 10000;
		User1 user2 = new User1();
		user2.setId(id);
		user2.setName("ok2");
		sqlManager.insert(user2);
		Assert.assertEquals(id,user2.getId().intValue());
		user2 = sqlManager.unique(User1.class,id);

    }

	@Test
	public void testSeq(){
		User2 user = new User2();
		user.setName("ok1");
		sqlManager.insert(user);
		Assert.assertNotNull(user.getId());


		user = new User2();
		user.setId(999);
		user.setName("ok2");
		sqlManager.insert(user);
		Assert.assertNotNull(user.getId());


	}

	@Data
	@Table(name="sys_user")
	public static class User1 {
		@AutoID
		Integer id;
		String name;
	}


	@Data
	@Table(name="sys_user")
	public static class User2 {
		@SeqID(name="my_sequence")
		Integer id;
		String name;
	}



}
