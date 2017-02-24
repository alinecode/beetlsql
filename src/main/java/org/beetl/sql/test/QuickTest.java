package org.beetl.sql.test;

import java.sql.Timestamp;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * 
 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		DB2SqlStyle style = new DB2SqlStyle();
//		OracleStyle style = new OracleStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		
		Interceptor[] inters = new Interceptor[]{ new DebugInterceptor()};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);

		UserDao dao = sql.getMapper(UserDao.class);
//		dao.all();
//		User user = new User();
//		user.setName("clk");
//		user.setCreateTime(new Timestamp(System.currentTimeMillis()));
//		user.setContent("elsdfsfsfsdf");
//		dao.insert(user,true);
//		System.out.println(user.getId());
		
//		User user = dao.unique(8);
//		System.out.println(user.getCreateTime());
		
		sql.genPojoCodeToConsole("USER");
		sql.genSQLTemplateToConsole("USER");
		
		
		
	
	}
	
	
}
