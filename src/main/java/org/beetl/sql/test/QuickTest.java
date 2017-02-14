package org.beetl.sql.test;

<<<<<<< HEAD
=======
import java.util.Date;
>>>>>>> aabd505f6f4c040c9a87d5aee3685f954938ba74
import java.util.List;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * 
 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
//		OracleStyle style = new OracleStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		
		Interceptor[] inters = new Interceptor[]{ new DebugInterceptor()};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);

		
		User query = new User();
		query.setCreateTime(new java.sql.Date(System.currentTimeMillis()));
		List list = sql.select("user.queryUsers", User.class, query);
		System.out.println(list.size());
		
		
		
		
		
	
	}
	
	
}
