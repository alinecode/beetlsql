package org.beetl.sql.test;



import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;





public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
	
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});

		User user = new User();
		user.setName("hello go");
//		KeyHolder h = new KeyHolder();
//		sql.insert(User.class, user, h);
//		sql.insert(user, true);
//		System.out.println(user.getId());
		UserDao dao = sql.getMapper(UserDao.class);
		dao.insert(user, true);
		System.out.println(user.getId());
		
		

	}

	
}
