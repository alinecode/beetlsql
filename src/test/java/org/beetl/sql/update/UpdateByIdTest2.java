/**
 * 
 */
package org.beetl.sql.update;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.pojo.User;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxinjie
 *
 */
public class UpdateByIdTest2 {

	private SQLLoader loader;
	private SQLManager manager;
	
	@Before
	public void before(){
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, 
				new MySqlConnectoinSource(),new UnderlinedNameConversion(),new Interceptor[]{new DebugInterceptor()});
}


	@Test
	public void updateById(){
		
		SysRole role = manager.unique(SysRole.class,1 );
		
		role.setName("admin");
		int i = manager.updateById(role);
		System.out.println(i);
	}
	
}
