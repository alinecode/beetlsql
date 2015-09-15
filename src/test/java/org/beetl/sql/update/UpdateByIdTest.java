/**
 * 
 */
package org.beetl.sql.update;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.User;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxinjie
 *
 */
public class UpdateByIdTest {

	private SQLLoader loader;
	private SQLManager manager;
	
	@Before
	public void before(){
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}

	/**
	 * TODO 未测试完：需要处理","的问题，可能会出现update set user name=#name#, wehre 1=1 and ....的情况
	 * 
	 * TODO 新增trim方法，修改后测试
	 */
	@Test
	public void updateById(){
		
		User user = new User();
		user.setId(4);
		user.setName("张三2222");
		user.setAge(100);
		user.setUserName("从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：");
		
		int i = manager.updateById(user);
		System.out.println(i);
	}
	
}
