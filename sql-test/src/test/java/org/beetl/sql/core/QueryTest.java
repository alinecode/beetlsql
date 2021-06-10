package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            List<User> users = lambdaQuery.andEq(User::getDepartmentId,2).orderBy(User::getId).select();
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

    /**
     * Group 在Query中不好用，因为代码里引用了列名字
     */
    @Test
    public void group(){
        List<Map> users = lambdaQuery.andIsNotNull(User::getId).groupBy(User::getDepartmentId).mapSelect("count(1) as total","department_id");
        //不确定数据库返回什么类型，因此用Number
        Number number = (Number)users.get(0).get("total");
        Assert.assertEquals(1,number.intValue());
    }

    @Test
    public void filter(){
        String name = null;
        long count  = lambdaQuery.andEq("name",Query.filterEmpty(name)).count();
        Assert.assertEquals(3,count);


		count  = lambdaQuery.andEq("name", Optional.ofNullable(name)).count();
		Assert.assertEquals(3,count);


		count  = lambdaQuery.andEq("name", Optional.ofNullable("lijz")).count();
		Assert.assertEquals(1,count);
    }






}
