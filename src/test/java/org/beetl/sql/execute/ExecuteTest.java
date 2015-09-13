package org.beetl.sql.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.buildsql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.Params;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.pojo.Role;
import org.beetl.sql.pojo.User;
import org.junit.Before;
import org.junit.Test;

public class ExecuteTest {
	private SQLLoader loader;
	private SQLManager manager;
	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource(),new  HumpNameConversion(),
				new Interceptor[]{new DebugInterceptor()});
	}
	
//	
//	@Test
//	public void testQuery(){
//		User user = new User();
//		List<User> list = manager.execute("select * from user", User.class, new HashMap());
//		System.out.println(list.size());
//	
//		 list = manager.execute("select * from user where name = #name#", User.class, Params.ins().add("name", "joel").map());
//		System.out.println(list.size());
//		User query = new User();
//		query.setName("gk_0");;
//		 list = manager.execute("select * from user where name = #name#", User.class, query);
//		 System.out.println(list.size());
//	}
//	
//	
//	@Test
//	public void testUpdate(){
//		Map map = new HashMap();
//		map.put("id",4);
//		int result = manager.executeUpdate("delete from user where id=#id#",map);
//		System.out.println(result);
//		User user = new User();
//		user.setId(4);
//		user.setName("old");
//		result = manager.executeUpdate("insert into user (id,name,age) values (#id#,#name#,#age#)",user);
//		
//		
//		System.out.println(result);
//		
//		
//	}
	
	@Test
	public void testQuery2(){
		String sql = "select * from user where id = #role.id#";
		User user = new User();
		Role role = new Role();
		role.setId(1);
		user.setRole(role);
		List list = manager.execute(sql, User.class, user);
		System.out.println(list);
	}
}
