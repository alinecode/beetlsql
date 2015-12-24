/**
 * 
 */
package org.beetl.sql.select;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Params;
import org.beetl.sql.core.RowMapper;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.Role;
import org.beetl.sql.pojo.User;
import org.junit.Before;
import org.junit.Test;
/**
 * @author suxinjie
 *
 */
public class SelectAllTest {

	private SQLLoader loader;
	private SQLManager manager;
	
	@Before
	public void before(){
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
//		manager.seti
	}

	//@Test
	public void selectAll(){
		
		List<User> userList = manager.all(User.class);
		for(User user : userList){
			System.out.println(user);
		}
		
	}
	
	
	@Test
	public void selectAllPage(){
		
		List<User> userList = manager.all(User.class,1,2);
		for(User user : userList){
			System.out.println(user);
		}
		
	}
	
//	@Test
	public void selectAll_RowMapper(){
		
		List<User> userList = manager.all(User.class, new RowMapper<User>() {

			@Override
			public User mapRow(Object o,ResultSet rs, int rowNum) throws SQLException {
				User u = (User)o;			
				Role r = manager.unique(Role.class, rs.getInt("roleId"));
				u.setRole(r);
				return u;
			}
		});
		for(User user : userList){
			System.out.println(user);
		}
		
	}
	
	
//	@Test
	public void selectLike(){
		
		List users = manager.execute("select * from user where name like #'%'+name+'%'#",
				User.class,Params.ins().add("name", "_1").map());
		System.out.println(users);
		
	}
	
//	@Test
	public void selectUse(){
		User para = new User();
		para.setName("name1");
		List users = manager.select("user.selectByExample", User.class, para);
		
		System.out.println(users);
		
	}
	
}