package org.beetl.sql.test;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.annotatoin.EnumMapping;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.test.Role.RoleType;


public class QuickTest {
	
	public static void main(String[] args) throws Exception{
		MySqlStyle style = new MySqlStyle();
	
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		sql.genPojoCodeToConsole("role");
		Role r1 = new Role();
		r1.setName("ttt");
		r1.setType(RoleType.EMPLOYEE);
		sql.insert(r1);
		
		Role r2 = sql.unique(Role.class, 1);
		System.out.println(r2.getType());
		
	}

	
}
