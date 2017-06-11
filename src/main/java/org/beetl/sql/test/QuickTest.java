package org.beetl.sql.test;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
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
		
//		String sql = "   select *\n    from\n user\n where 1=1";
//		sql = sql.replaceAll("--.*", "").replaceAll("\\s+", " ");
//		System.out.println(sql);
		
		
//		DB2SqlStyle style = new DB2SqlStyle();
		MySqlStyle style = new MySqlStyle();
//		OracleStyle style = new OracleStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		
		Interceptor[] inters = new Interceptor[]{ new DebugInterceptor()};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);
		UserDao dao = sql.getMapper(UserDao.class);
		PageQuery query = dao.getUser4(1, "4");
//		String  jdbcSql = "  select *from user order by id";
//		PageQuery query = new PageQuery(1);
//		sql.execute(new SQLReady(jdbcSql), User.class, query);
		
		System.out.println(query.getTotalPage());
		System.out.println(query.getTotalRow());
		System.out.println(query.getList());
//	
			
	}
	
	
}


