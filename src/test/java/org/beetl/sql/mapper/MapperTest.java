package org.beetl.sql.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.pojo.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MapperTest {


	SQLManager manager = null;
	UserDao dao = null;
	User newUser = null;
	
	@Before
	public void setUp() throws Exception {
		ClasspathLoader loader = new ClasspathLoader("/sql/");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource(),new DefaultNameConversion(),new Interceptor[]{new DebugInterceptor()});
		dao = manager.getMapper(UserDao.class);
		newUser = new User();
		newUser.setName("junit");
		newUser.setAge(12);
	}

	@Test
	public void insert() {
		
		
		dao.insert(newUser);
		Assert.assertNotNull(dao.insertReturnKey(newUser).getInt());
		dao.insert(newUser, true);
		
		Assert.assertNotNull(newUser.getId());
		
		
	}
	
	@Test
	public void queryInner() {
		
		long count = dao.allCount();
		List<User> user = dao.all();
		Assert.assertEquals(count, user.size());
		
		User template = new User();
		template.setName("junit");
		List<User> ret = dao.template(template);
		Assert.assertNotEquals(ret.size(), 0);
		
		int id = dao.insertReturnKey(newUser).getInt();
		User myUser = dao.unique(id);
		Assert.assertNotNull(myUser);//实际上找不到会抛异常，而不是返回null
		myUser.setAge(15);
		dao.updateById(myUser);
		myUser = dao.unique(id);
		Assert.assertEquals((Object)myUser.getAge(), (Object)15);
		
		
		dao.deleteById(id);
	}
	
	@Test
	public void testSqlId(){
		int id = dao.insertReturnKey(newUser).getInt();
		User user = dao.findById(id);
		Assert.assertNotNull(user);
		dao.setAge(id, 15);
		user = dao.findById(id);
		Assert.assertEquals((Object)user.getAge(), (Object)15);
		Map map = new HashMap();
		map.put("id", id);
		map.put("age", 18);
		map.put("name", user.getName());
		dao.setUserStatus(map);
		user = dao.findById(id);
		Assert.assertEquals((Object)user.getAge(), (Object)18);
		List<User> list = dao.queryUser("junit", null, 1, 2);
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(dao.getCount(),dao.allCount());
		
		List<User> batch = new ArrayList<User>();
		batch.add(user);
		dao.setUserStatus(batch);
		dao.deleteById(id);
		
	}
	
}