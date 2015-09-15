/**
 * 
 */
package org.beetl.sql.update;

import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.pojo.User;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxinjie
 *
 */
public class UpdateAllTest {

	private SQLLoader loader;
	private SQLManager manager;
	
	@Before
	public void before(){
		loader = new ClasspathLoader("/sql/mysql");
		//manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource(), new HumpNameConversion(), new Interceptor[]{new DebugInterceptor()});
	}

	/**
	 * TODO 未测试完：需要处理","的问题，可能会出现update set user name=#name#, wehre 1=1 and ....的情况
	 * 
	 * TODO 新增trim方法修改后测试
	 */
//	@Test
	public void updateAll(){
		
		User user = new User();
//		user.setId(4);
//		user.setName("14");
		user.setAge(11);
//		user.setUserName("aaa");
		
		int i = manager.updateAll(User.class, user);
		System.out.println(i);
	}
	
	
	@Test
	public void updateName(){
		
		User user = new User();
//		user.setAge(12);
		user.setName("hellogo");
		int i = manager.update("user.updateName", user);
		System.out.println(i);
	}
	
	@Test
	public void batchUpdateName(){
		
		User user = new User();
		user.setAge(7);
		user.setName("hellogo123");
		
		List<User> list = new ArrayList<User>();
		list.add(user);
		
		
		
		
		int[] i = manager.updateBatch ("user.updateName", list);
		System.out.println(i);
	}
	
	

}
