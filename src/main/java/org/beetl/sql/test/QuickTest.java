package org.beetl.sql.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.ext.DebugInterceptor;

public class QuickTest {

	public static void main(String[] args) throws Exception{
		SQLManager mysql = getMySql();
		SQLManager postgres = getPostgres();
		UserDao dao = mysql.getMapper(UserDao.class);
		Map map = new HashMap();
		map.put("id", 4);
		map.put("age", 12);
		dao.setUserStatus(map, "lijz");
	}
	
	private static SQLManager getMySql(){
		MySqlStyle style = new MySqlStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");

		SQLManager 	mysql = new SQLManager(style,loader,cs,new DefaultNameConversion(), new Interceptor[]{new DebugInterceptor()});
		return mysql;
			
	}
	
	private static SQLManager getPostgres(){
		PostgresStyle style = new PostgresStyle();
		PostgresConnectoinSource cs = new PostgresConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	postgres = new SQLManager(style,loader,cs,new DefaultNameConversion(), new Interceptor[]{new DebugInterceptor()});
		return postgres;
			
	}

}
