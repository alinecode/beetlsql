package org.beetl.sql.oracle;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.beetl.sql.OracleConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Before;
import org.junit.Test;

public class InsertTest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		OracleStyle style = new OracleStyle();
		loader = new ClasspathLoader("/sql/",style);
		manager = new SQLManager(new OracleStyle(), loader, new OracleConnectoinSource(),new  UnderlinedNameConversion(),
				new Interceptor[]{new DebugInterceptor()});
		
	}

	
//	public void addUser
	@Test
	public void addUser() throws Exception {
		
//		Connection conn = manager.getDs().getMaster();
//		DatabaseMetaData dbmd =  conn.getMetaData();
//		
//		ResultSet rs = dbmd.getTables(null, "HR", "dept",
//				new String[] { "TABLE" });
//		while(rs.next()) {
//		    System.out.println(rs.getString("TABLE_NAME"));
//		}
//		
//
//		Dept dept = new Dept();
//		dept.setName("ok");
//		KeyHolder holder = new KeyHolder();
//		manager.insert(Dept.class, dept,holder);
		
//		UserInfo info = new UserInfo();
//		info.setId(1);
//		info.setUserName("cl");
//		KeyHolder holder = new KeyHolder();
//		manager.insert(UserInfo.class,info,holder);
//		System.out.println(holder.getKey());
		
//		manager.template(info);
		
////		List<Dept> list = manager.execute("select * from dept", Dept.class, new HashMap());
//		System.out.println(list);
		
//		manager.insert("dept.insert", dept, holder);
//		System.out.println("kye is"+holder.getLong());
		
		manager.genSQLTemplateToConsole("hr.dept");
	}
	

}