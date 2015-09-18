package org.beetl.sql.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.Role;
import org.junit.Before;
import org.junit.Test;

public class KeyTest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}

	
//	public void addUser
	@Test
	public void addRole() {

		Role role = new Role();
		role.setName("manager");
		KeyHolder holder = new KeyHolder();
		manager.insert(Role.class, role, holder);
		
		System.out.println(holder.getKey());
		
	}
	
	@Test
	public void addRole1() {
		
		try{
			Connection conn = new MySqlConnectoinSource().getMaster();
			PreparedStatement ps = conn.prepareStatement("update role set name =? where id=?");
			Role role = new Role();
			role.setId(1);
			role.setName("ac");
			List<Object> list = new ArrayList<Object>();
			list.add(role);
			list.add(role);
			for(int i=0;i<list.size();i++){
				Role data = (Role)list.get(i);
				ps.setString(1, data.getName());
				ps.setInt(2, data.getId());
				ps.addBatch();
			}
			int[] result = ps.executeBatch();
			for(int i : result){
				System.out.println(i);
			}
			
		}catch(Exception ex){
			
		}
		
	}
}
