package org.beetl.sql.test;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
<<<<<<< HEAD
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.core.engine.PageQuery;
=======
import org.beetl.sql.core.db.MySqlStyle;
>>>>>>> 462aac45587f2fa5f4feea977e9cfa0320c89620
import org.beetl.sql.ext.DebugInterceptor;

/**
 * 
 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{

//		DB2SqlStyle style = new DB2SqlStyle();
		MySqlStyle style = new MySqlStyle();
//		OracleStyle style = new OracleStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		
		Interceptor[] inters = new Interceptor[]{ new DebugInterceptor()};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);

		UserDao dao = sql.getMapper(UserDao.class);
<<<<<<< HEAD
		PageQuery query = new  PageQuery();
		query.setPageNumber(2);
		query.setPageSize(2);
		query.setOrderBy("id desc");
		dao.queryUsers(query);
		int a = 1;
=======
//		dao.all();
		sql.update("user.updateUser");
		
//		User user = dao.unique(8);
//		System.out.println(user.getCreateTime());
		
//		sql.genPojoCodeToConsole("USER");
//		sql.genSQLTemplateToConsole("USER");
>>>>>>> 462aac45587f2fa5f4feea977e9cfa0320c89620
		
		
		
	
	}
	
	
}
