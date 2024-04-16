package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.BuilderTest;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.core.query.QueryCondition;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;

public class QueryTest extends BaseTest {
    Query<User> query = null;
    LambdaQuery<User> lambdaQuery = null;
    @BeforeClass
    public static void init() {
        initTable(testSqlFile);
    }

    @Before
    public void initQuery(){
        query = sqlManager.query(User.class);
        lambdaQuery = sqlManager.lambdaQuery(User.class);
    }

    @Test
    public void andEq() {
        User user = lambdaQuery.andEq(User::getId,1).single();
        Assert.assertNotNull(user);
        user = lambdaQuery.andEq(User::getName,"lijz").single();
        Assert.assertNotNull(user);
        user = lambdaQuery.andEq(User::getId,1).andEq(User::getDepartmentId,9999).single();
        Assert.assertNull(user);

    }

    @Test
    public void selectCol() {
        List<User> users = lambdaQuery.andEq(User::getId,1).select(User::getId,User::getName);
        User user = users.get(0);
        Assert.assertNotNull(user.getName());
        Assert.assertNull(user.getDepartmentId());

    }

    @Test
    public void order() {
        {
            List<User> users = lambdaQuery.andEq(User::getDepartmentId,2).asc(User::getId).select();
            User user = users.get(0);
            User user1 = users.get(1);
            Assert.assertTrue(user1.getId()>user.getId());
        }
        {
            List<User> users = lambdaQuery.andEq(User::getDepartmentId, 2).desc(User::getId).select();
            User user = users.get(0);
            User user1 = users.get(1);
            Assert.assertTrue(user1.getId() < user.getId());
        }

    }

    @Test
    public void testCondition(){
        List<User> list = lambdaQuery.andIn(User::getId, Arrays.asList(2,3))
                .or(lambdaQuery.condition().andEq(User::getName,"lijz")
                        .orEq(User::getDepartmentId,1))
                .select();
        Assert.assertEquals(3,list.size());
    }

    @Test
    public void like(){
        long count = lambdaQuery.andLike(User::getName,"%li%").count();
        Assert.assertEquals(1,count);

    }

    @Test
    public void page(){
        List<User> users = lambdaQuery.limit(1,5).select();
        Assert.assertEquals(3,users.size());
    }

	@Test
	public void page2(){
		PageResult<User> page = lambdaQuery.andLike(User::getName,"%li%").page(1,10);
		Assert.assertEquals(1,page.getTotalPage());
	}



    @Test
    public void filter(){
        String name = null;
        long count  = lambdaQuery.andEq("name",Query.filterEmpty(name)).count();
        Assert.assertEquals(3,count);


		count  = lambdaQuery.andEq(User::getName, Optional.ofNullable(name)).count();
		Assert.assertEquals(3,count);


		count  = lambdaQuery.andEq("name", Optional.ofNullable("lijz")).count();
		Assert.assertEquals(1,count);

		count = lambdaQuery.andNotBetween("name",Query.filterEmpty("aaa"),Query.filterEmpty(name)).count();

    }


	@Test
	public void group(){

		Query<User> query = sqlManager.query(User.class);
		List<User> list = query
				.andIn("id", Arrays.asList(1, 2))
				.groupBy("name")
				.select();
		Assert.assertEquals(2,list.size());


	}



	@Test
	public void reuse(){

		QueryCondition<User> condition = sqlManager.query(User.class);
		condition.andIn("id", Arrays.asList(1, 2));
		Query<User> newQuery = sqlManager.query(User.class).useCondition(condition);
		long count = newQuery.count();

		List<User> users = sqlManager.query(User.class).useCondition(condition).limit(1,10).select();
		Assert.assertEquals(2,count);
		Assert.assertEquals(2,users.size());


	}


	@Test
	public void stream() throws SQLException {
		//模拟事务环境
		DSTransactionManager.start();
		Query<User> query = sqlManager.query(User.class);
		StreamData<User> list = query
				.andIn("id", Arrays.asList(1, 2))
				.groupBy("name")
				.stream();
		List allData = new ArrayList();
		list.foreach( user -> {
			allData.add(user);
		});

		Assert.assertEquals(2,allData.size());
		DSTransactionManager.commit();
	}

	@Test
	public void condition() throws SQLException {
		//模拟事务环境

		Query<User> query = sqlManager.query(User.class);
		QueryCondition condition =  query.condition().andEq("id",2);
		List<User> users = query.andEq("id",1).or(condition).select();
		Assert.assertEquals(2,users.size());
	}





}
