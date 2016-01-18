package org.beetl.sql.test;

import java.math.BigDecimal;

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
		
//		List<User2> user = sql.select("user2.selectSum", User2.class, null);
		BigDecimal data  = sql.bigDecimalValue("user2.selectSum", null);
		sql.all(User2.class);
		
//		User2 user = new User2();
//		user.setUsername("ok");
//		user.setSalary(new BigDecimal("123"));
//		sql.insert(user);
//		sql.genSQLTemplateToConsole("jfinal_demo.blog");
//		sql.genPojoCodeToConsole("jfinal_demo.blog");
//		User info = new User();
//		info.setName("aa");
//		info.setAge(12);
//		KeyHolder h = new KeyHolder();
//		sql.insert("user.insert", info, h);
//		System.out.println(h.getLong());
		


	}

}
