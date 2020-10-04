package org.beetl.sql.core.mapper;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.annotation.Template;
import org.beetl.sql.mapper.internal.InsertAMI;
import org.junit.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 测试mapper基本用法
 */
public class MapperTest extends BaseTest {
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }


    @Test
    public void simpleMapperTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        long count = dao.allCount();
        Assert.assertEquals(3,count);
        boolean exist = dao.exist(1);
        Assert.assertTrue(exist);
        User user = dao.unique(1);
        user.setName(user.getName());
        dao.updateById(user);

    }

    @Test
    public void templateTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        User user = dao.queryTemplateById(1);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getId().intValue(),1);
    }


    @Test
    public void jdbcSqlTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        User user = dao.querySqlById(1);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getId().intValue(),1);
    }

    @Test
    public void updateTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        int ret =  dao.updateName(1,"lijz");
        Assert.assertTrue(ret==1);
        ret =  dao.updateName(10,"lijz");
        Assert.assertTrue(ret==0);
    }

    @Test
    public void providerSqlTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        User user = dao.queryProviderById(1);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getId().intValue(),1);
    }


    @Test
    public void providerTemplateSqlTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        User user = dao.queryTemplateProviderById(1);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getId().intValue(),1);
    }

    @Test
    public void myBaseMapperTest(){
        MyUserMapper myUserMapper = sqlManager.getMapper(MyUserMapper.class);
        User user = new User();
        user.setName("test");
        myUserMapper.insert(user);

        String msg = myUserMapper.helloWorld(user);
        Assert.assertEquals("hello",msg);

        user = myUserMapper.queryTemplateById(user.getId());
        Assert.assertEquals("test",user.getName());
    }

    @Test
    public void springDataTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        User user = dao.getById(1);
        Assert.assertEquals(1,user.getId().intValue());
        List<User> users = dao.getByName("lijz");
        Assert.assertEquals(1,users.size());
        Assert.assertEquals("lijz",users.get(0).getName());
        users = dao.getByAgeOrNameOrderByIdAsc(12,"lijz");
        Assert.assertEquals(2,users.size());
        Assert.assertEquals(1,users.get(0).getId().intValue());
    }

    @Test
    public void defaultMethodTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        int count = dao.count(12);
        Assert.assertEquals(2,count);
    }

    @Test
    public void pageTest(){
        UserDao dao = sqlManager.getMapper(UserDao.class);
        PageRequest request = DefaultPageRequest.of(1,10);
        PageResult result = dao.page("lijz",12,request);
        Assert.assertEquals(1,result.getTotalRow());
        Assert.assertEquals(1,result.getTotalPage());
    }


    public static interface MyBaseMapper<T> {
        @AutoMapper(InsertAMI.class)
        void insert(T entity);
        @AutoMapper(HelloAMI.class)
        String helloWorld(T entity);
    }

    public static interface MyUserMapper extends MyBaseMapper<User>{
        @Template("select * from sys_user where id=#{id}")
        public User queryTemplateById(Integer id);

    }


    public static class HelloAMI extends MapperInvoke {

        @Override
        public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
            return "hello";
        }
    }





}
