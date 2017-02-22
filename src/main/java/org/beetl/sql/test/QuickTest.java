package org.beetl.sql.test;

import java.util.HashMap;
import java.util.Map;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.PageQuery;
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

		UserDao dao = sql.getMapper(UserDao.class);
//		Test test = new Test();
//		sql.insert(test);
		
		PageQuery query = new PageQuery();
		dao.queryUsers(query, "hello");
		
		
		
		
		
	
	}
	
	
}
