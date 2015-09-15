/**
 * 
 */
package org.beetl.sql.delete;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.LobBean;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxinjie
 *
 */
public class LobDeleteTest {

	private SQLLoader loader;
	private SQLManager manager;

	@Before public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}
	
	@Test public void deleteLob(){
		int i = manager.deleteById(LobBean.class, 8);
		System.out.println(i);
	}

}
