package org.beetl.sql.test;



import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.SimpleCacheInterceptor;



/**
 * 

 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		List<String> lcs = new ArrayList<String>();
		lcs.add("user");
		SimpleCacheInterceptor cache =new SimpleCacheInterceptor(lcs);
		Interceptor[] inters = new Interceptor[]{ new DebugInterceptor(),cache};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);

		List<User> list = sql.select("user.queryUsers", User.class, null);
		User user = list.get(0)
;		Department dept = (Department)user.get("department");
		System.out.println(dept.getName());
		List<Role> roles  = (List<Role>)user.get("role");
		System.out.println(roles.size());
	}
	
	
}
