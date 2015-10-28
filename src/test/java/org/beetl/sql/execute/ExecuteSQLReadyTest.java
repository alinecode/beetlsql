package org.beetl.sql.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.Params;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.pojo.Role;
import org.beetl.sql.pojo.User;
import org.junit.Before;
import org.junit.Test;

public class ExecuteSQLReadyTest {
	private SQLLoader loader;
	private SQLManager manager;
	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource(),new  DefaultNameConversion(),
				new Interceptor[]{new DebugInterceptor()});
	}
	
	
//	@Test
	public void testQuery(){
		SQLReady ready = new SQLReady("select * from user w where id = ?",1);
		List<User> list = manager.execute(ready,User.class);
		System.out.println(list.size());

	}
	
	
	@Test
	public void testUpdate(){
		SQLReady ready = new SQLReady("delete from user where id=?",1);
		int result = manager.executeUpdate(ready);
		System.out.println(result);
	
	}
	
}
