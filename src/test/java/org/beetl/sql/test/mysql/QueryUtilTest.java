package org.beetl.sql.test.mysql;

import org.beetl.sql.core.query.Query;
import org.beetl.sql.core.query.QueryTool;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class QueryUtilTest extends BaseMySqlTest {

    @Before
    public  void init(){
        super.init();
        QueryTool.init(super.sqlManager);
    }
    @Test
    public void testSelect() {
        Query<User> query = new Query<User>(User.class);
        List<User> list = query.select();
        assert !list.isEmpty();
    }

    @Test
    public void testSelectColumns() {
        Query<User> query = new Query<User>(User.class);
        List<User> list = query.select("name","id");
        assert !list.isEmpty();
    }

    @Test
    public void testSelectCondition() {
        Query<User> query = new Query<User>(User.class);
        List<User> list = query.andEq("id",1637)
                .andLess("create_time",new Date())
                .andEq("name","test")
                .select("name","id");
        assert !list.isEmpty();
    }

    @Test
    public void testUpdateCondition() {
        User record = new User();
        record.setName("new name");
        Query<User> query = new Query<User>(User.class);
        int count = query.andEq("id",1637)
                .andLess("create_time",new Date())
                .andEq("name","test")
                .update(record);

        assert count != 0;
    }
}
