package org.beetl.sql.test.mysql;

import org.beetl.sql.core.mapper.internal.UserDao;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class QueryUtilTest extends BaseMySqlTest {

    @Before
    public void init() {
        super.init();
    }

//    @Test
//    public void testLambda(){
//        LambdaQuery<User> query = sqlManager.lambdaQuery(User.class);
//        List<User> list = query.andEq(User::getName, "user7").or(query.condition().andEq(User::getId, 1641)).select();
//        assert list.size()==2;
//    }

    @Test
    public void testSelect() {
        Query<User> query = sqlManager.query(User.class);
        List<User> list = query.andBetween("id", 1, 1640)
                .andLike("name", "%t%")
                .andIsNotNull("create_time")
                .orderBy("id desc").select();
        assert !list.isEmpty();
    }

    @Test
    public void testSelect2() {
        Query<User> query = sqlManager.query(User.class);
        List<User> list = query
                .or(query.condition()
                        .andLike("name", "%t%"))
                .andIn("id", Arrays.asList(1637, 1639, 1640))
                .or(query.condition().andEq("id", 1640))
                .select();

        assert !list.isEmpty();
    }

    @Test
    public void testSelect3() {
        Query<User> query = sqlManager.query(User.class);
        List<User> list = query
                .and(query.condition()
                        .andIn("id", Arrays.asList(1637, 1639, 1640))
                        .andLike("name", "%t%"))
                .andEq("id", 1640)
                .or(query.condition().andEq("name", "new name2"))
                .select();

        assert !list.isEmpty();
    }

    @Test
    public void testSelectGroup() {
        Query<User> query = sqlManager.query(User.class);
        List<User> list = query
                .andIn("id", Arrays.asList(1637, 1639, 1640))
                .groupBy("name")
                .having(query.condition().andIsNotNull("create_time"))
                .select();

        assert !list.isEmpty();
    }

    @Test
    public void testSelectColumns() {
        Query<User> query = sqlManager.query(User.class);
        List<User> list = query.select("name", "id");
        assert !list.isEmpty();
    }

    @Test
    public void testSelectCondition() {
        Query<User> query = sqlManager.query(User.class);
        List<User> list = query.andEq("id", 1637)
                .andLess("create_time", new Date())
                .andEq("name", "test")
                .select("name", "id");
        assert !list.isEmpty();
    }

    @Test
    public void testUpdateAbsCondition() {
        User record = new User();
        record.setName("new name");
        Query<User> query = sqlManager.query(User.class);
        int count = query.andEq("id", 1637)
                .andLess("create_time", new Date())
                .andEq("name", "test")
                .update(record);

        assert count != 0;
    }


    @Test
    public void testUpdateMoreEqCondition() {
        User record = new User();
        record.setName("new name");
        Query<User> query = sqlManager.query(User.class);
         query.andEq("id", 1637)
                .andEq("name", "xxxx")
                .andEq("name", "test")
                .select();

        query.updateSelective(record);
        int count = 1;
        assert count != 0;
    }


    @Test
    public void testUpdateCondition() {
        User record = new User();
        record.setName("new name");
        Query<User> query = sqlManager.query(User.class);
        int count = query.andEq("id", 1637)
                .andLess("create_time", new Date())
                .andEq("name", "test")
                .updateSelective(record);

        assert count != 0;
    }

    @Test
    public void testInsertAbsCondition() {
        User record = new User();
        record.setName("new name");
        Query<User> query = sqlManager.query(User.class);
        int count = query.insert(record);

        assert count != 0;
    }

    @Test
    public void testInsertCondition() {
        User record = new User();
        record.setName("new name2");
        record.setCreateTime(new Date());
        Query<User> query = sqlManager.query(User.class);
        int count = query.insertSelective(record);

        assert count != 0;
    }

    @Test
    public void testDeleteCondition() {
        Query<User> query = sqlManager.query(User.class);
        int count = query.andEq("id", 1642).delete();
        assert count != 0;
    }

    @Test
    public void testCountCondition() {
        User record = new User();
        record.setName("new name");
        Query<User> query = sqlManager.query(User.class);
        long count = query.andEq("name", "new name")
                .orEq("id", 1637).limit(1, 10).count();
        assert count != 0;
    }

    @Test
    public void single() {
        Query<User> query = sqlManager.query(User.class);
        User user = query.andEq("id", 1642).single();
        assert user != null;
    }

    @Test
    public void testInSql() {
        Query<User> query = sqlManager.query(User.class);
        List<User> user = query.andEq("id", 1642).orBetween("id",1,3)
                .or(query.condition().andIn("id",Arrays.asList(5,6,7,8)))
                .and(query.condition().andBetween("id",9,22))
                .select();
        assert user != null;
    }
    
    // 混合
//    @Test
//    public void testLambda2Query() {
//        LambdaQuery<User> query = sqlManager.lambdaQuery(User.class);
//        List<User> user = query.andEq("id", 1642).orBetween(User::getId,1,3)
//                .or(query.condition().andIn("id",Arrays.asList(5,6,7,8)))
//                .and(query.condition().andBetween("id",9,22))
//                .select();
//        assert user != null;
//    }
//    
    // 重用Query
//    @Test
//    public void resueQuery() {
//        LambdaQuery<User> query = sqlManager.lambdaQuery(User.class);
//        User user = query.andEq(User::getId, 1641).single();
//        user = query.andEq(User::getId, 1642).single();
//    }
    
    
//    @Test
//    public void mapperDao() {
//        UserDao dao = sqlManager.getMapper(UserDao.class);
//        
//        LambdaQuery<User> query = dao.createLambdaQuery();
//        User user = query.andEq(User::getId, 1641).single();
//    }

}