package org.beetl.sql.test;

import java.util.List;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.DebugInterceptor;

public class QuickTest {

	public static void main(String[] args) throws Exception{
		MySqlStyle style = new MySqlStyle();
	
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");

		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		sql.genSQLTemplateToConsole("user");
				UserDao dao = sql.getMapper(UserDao.class);
		PageQuery query = new PageQuery();
		query.setPageSize(5);
		dao.queryNewUser(query);
		System.out.println(query.getTotalPage());
		System.out.println(query.getTotalRow());
		System.out.println(query.getPageNumber());
		List<User> list = query.getList();
		System.out.println("结果"+list.size());
//		query.setPageNumber(query.getPageNumber()+1);
////		sql.pageQuery("user.queryNewUser", User.class,query);
//		dao.queryNewUser(query);
//		list = query.getList();
//		System.out.println("结果"+list.size());
	}

}
