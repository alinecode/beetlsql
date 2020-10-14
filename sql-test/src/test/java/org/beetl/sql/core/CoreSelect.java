package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试内置的查询操作
 */
public class CoreSelect extends BaseTest {
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }

    @Test
    public void testUnique(){
        try{
            User user =  sqlManager.unique(User.class,1);
        }catch(Exception ex){
            ex.printStackTrace();
            Assert.fail();
        }
       User user1 = sqlManager.single(User.class,1);
       Assert.assertNotNull(user1);
    }

    @Test
    public void testAll(){
        List<User> users = sqlManager.all(User.class);
        Assert.assertEquals(3,users.size());
        long count = sqlManager.allCount(User.class);
        Assert.assertEquals(3l,count);

		users = sqlManager.all(User.class,1,5l);
		Assert.assertEquals(3,users.size());

    }


    @Test
    public void testTemplate(){
        User template = new User();
        template.setName("lijz");
        List<User> users = sqlManager.template(template);
        Assert.assertEquals(1,users.size());
        long count = sqlManager.templateCount(template);
        Assert.assertEquals(1l,count);
        User user = sqlManager.templateOne(template);
        Assert.assertEquals("lijz",user.getName());

    }

    @Test
    public void testTemplateSql(){
        String sql = "select * from sys_user where id=#{id}";
        Map map = new HashMap();
        map.put("id",1);
        List<User> list = sqlManager.execute(sql,User.class,map);
        Assert.assertEquals(list.size(),1);

    }


    @Test
    public void testJdbcSql(){
        String sql = "select * from sys_user where id=? or id = ?";
        SQLReady sqlReady = new SQLReady(sql,1,2);
        List<User> list = sqlManager.execute(sqlReady,User.class);
        Assert.assertEquals(list.size(),2);

    }


    @Test
    public void testSql(){
        Map map = new HashMap();
        map.put("id",1);
        List<User> list = sqlManager.select(SqlId.of("user","queryById"),User.class,map);
        Assert.assertEquals(list.size(),1);
    }


    @Test
    public void testPage(){
        Map map = new HashMap();
        PageRequest request = DefaultPageRequest.of(1,10);
        SqlId sqlId = SqlId.of("user.queryByCondition");
        PageResult result = sqlManager.pageQuery(sqlId,
                User.class,map,request);
        Assert.assertEquals(result.getTotalRow(),3);
        Assert.assertEquals(result.getList().size(),3);
    }



}
