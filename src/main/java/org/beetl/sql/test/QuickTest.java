package org.beetl.sql.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

public class QuickTest {

	public static void main(String[] args) throws Exception{
		MySqlStyle style = new MySqlStyle();
	
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");

		SQLManager 	sql = new SQLManager(style,loader,cs,new DefaultNameConversion(), new Interceptor[]{new DebugInterceptor()});
		List<String> list = sql.execute("select name from user where 1=1", String.class, null);
		System.out.println(list);
//		User user  = sql.unique(User.class, 1555);
//		System.out.println("kk"+user.getName());
	}

}
