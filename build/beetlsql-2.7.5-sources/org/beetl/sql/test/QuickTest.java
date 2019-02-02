package org.beetl.sql.test;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.PageQuery;
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
		PageQuery<User> query = new PageQuery<User>();
		UserDao dao = sql.getMapper(UserDao.class);
		dao.queryUsers(query);
		List<User> list = query.getList();
		Department dept = (Department)list.get(0).get("department");
		
		System.out.println(dept.getName());
		sql.genPojoCodeToConsole("user");
	}
	
	
}
