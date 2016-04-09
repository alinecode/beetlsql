package org.beetl.sql.test;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;


public class QuickTest {
	
	public static void main(String[] args) throws Exception{
		
		int a = (true?0:1)+5;
		System.out.println(a);
//		MySqlStyle style = new MySqlStyle();
//	
//		MySqlConnectoinSource cs = new MySqlConnectoinSource();
//		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
//		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		sql.genPojoCodeToConsole("role");
//		List list = new ArrayList();
//		User user1 = new User();
//		user1.setName("a");
//		list.add(user1);
//		list.add(user1);
//		Map paras = new HashMap();
//		paras.put("list", list);
//		sql.update("user.insertBatch", paras);
//		User user1 = new User();
//		user1.setName("a");
//		user1.setId(3);
//		sql.updateById(user1);
		
	}

	
}
