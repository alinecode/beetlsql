/**
 * 
 */
package org.beetl.sql.update;

import java.util.ArrayList;
import java.util.List;

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
public class UpdateByIdBatchTest {

	private SQLLoader loader;
	private SQLManager manager;
	
	@Before
	public void before(){
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}

	/**
	 * TODO  测试批量修改
	 * 
	 */
	@Test
	public void updateById(){
		List<User> list = new ArrayList<User>();
		for(int i = 0;i<20;i++){
			User user = new User();
			user.setId(i+1);
			user.setName("gk_"+i);
			user.setAge(i);
			list.add(user);
		}
		int[] i = manager.updateByIdBatch(list);
	}
	
}
