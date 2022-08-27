package org.beetl.sql.core.mapper;

import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.JsonMapper;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.annotation.InheritMapper;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.mapper.annotation.Template;
import org.beetl.sql.mapper.internal.InsertAMI;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 测试mapper中使用@Root和Param注解
 */
public class Mapper2Test extends BaseTest {
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }


    @Test
    public void simpleMapperTest(){
        User root = new User();
        root.setId(1);
        root.setName("lijz");
        root.setAge(12);

        User user = new User();
        user.setId(3);
        user.setName("用户三");
        user.setAge(18);

        UserDao2 dao = sqlManager.getMapper(UserDao2.class);
        User ret  = dao.queryByName(root);
        Assert.assertTrue(ret.getId()==1);

        ret  = dao.queryByName2(root);
        Assert.assertTrue(ret.getId()==1);


        ret  = dao.queryByName3(root);
        Assert.assertTrue(ret.getId()==1);

        ret  = dao.queryByName4(root,user);
        Assert.assertNull(ret);

        ret  = dao.queryByName6(root);
        Assert.assertTrue(ret.getId()==1);


        ret  = dao.queryByName7(root,user);
        Assert.assertTrue(ret.getId()==3);

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



	@Test
	public void mapperSqlLengthCheck(){
		User root = new User();
		root.setId(1);
		root.setName("lijz");
		root.setAge(12);
		UserDao2 dao = sqlManager.getMapper(UserDao2.class);
		try{
			//过长的mapper会抛出异常
			User ret  = dao.queryByName8(root);
		}catch(BeetlSQLException beetlSQLException){
			beetlSQLException.printStackTrace();
			Assert.assertEquals(BeetlSQLException.MAPPER_SQL_LIMIT,beetlSQLException.getCode());
			return ;
		}

		Assert.fail();

	}


	@Test
	public void testTemplateBatchUpdate(){
		UserDao2 dao = sqlManager.getMapper(UserDao2.class);
		User user1 = new User();
		user1.setName("a");
		user1.setId(1);

		User user2 = new User();
		user2.setName("b");
		user2.setId(2);
		List<User> users = Arrays.asList(user1,user2);
		dao.batchTemplateUpdate(users);
	}


    @Test
    public void mapperInheritMapper(){

        MyTestUserMapper dao = sqlManager.getMapper(MyTestUserMapper.class);
        try{
            dao.implementByChild();
        }catch(RuntimeException re){
            re.printStackTrace();
            Assert.fail();
        }



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


    public static interface  CommonMapper<T> extends BaseMapper{
        @InheritMapper
        public List<T> implementByChild();
    }

    @SqlResource("user")
    public static interface  MyTestUserMapper extends CommonMapper<User>{

    }







}
