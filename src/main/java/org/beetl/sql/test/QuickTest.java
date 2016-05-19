package org.beetl.sql.test;

import java.util.List;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.gen.GenConfig;


public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
	
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
		
		GenConfig config = new GenConfig();
		config.setPreferDate(true);
//		sql.genPojoCodeToConsole("ok",config);
//		sql.genSQLTemplateToConsole("ok");
		
		Ok ok = new Ok();
		ok.setAge("a");
		ok.setName("bc");
		sql.insert(ok);
		
		List<Ok> ls = sql.all(Ok.class);
		System.out.println(ls);
		
//		UserDao dao = sql.getMapper(UserDao.class);
////		List<String> list = dao.getNames();
////		System.out.println(list);
//		
//		List<String> list = dao.getMyNames("%gk%");
//		System.out.println(list);
		
//		User user = new User();
//		user.setId(28);
//		user.setName("lijz");
//		KeyHolder h = new KeyHolder();
//		dao.insertTestUser(user);
//		System.out.println(h.getKey());
	}

	
}
