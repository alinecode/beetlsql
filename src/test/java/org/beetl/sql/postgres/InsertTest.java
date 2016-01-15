package org.beetl.sql.postgres;

import java.util.Date;

import org.beetl.sql.PostgresConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Before;
import org.junit.Test;

public class InsertTest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/");
		manager = new SQLManager(new PostgresStyle(), loader, new PostgresConnectoinSource(),new  HumpNameConversion(),
				new Interceptor[]{new DebugInterceptor()});
	}

	

//	@Test
	public void addCompany() throws Exception {
		
//		Connection conn = manager.getDs().getMaster();
//		DatabaseMetaData dbmd =  conn.getMetaData();
//		
//		ResultSet rs = dbmd.getTables(null, "%", "DEPT",
//				new String[] { "TABLE" });
//		while(rs.next()) {
//		    System.out.println(rs.getString("TABLE_NAME"));
//		}
		

		Company2 dept = new Company2();
		dept.setName("ok");
		dept.setDate(new Date());
		manager.insert(Company2.class, dept);
////		List<Dept> list = manager.execute("select * from dept", Dept.class, new HashMap());
//		System.out.println(list);
		
		
	}
	
//	@Test
//	public void addUser() throws Exception {
//		
//		User user = new User();
//		user.setAge(12);
//	
//		manager.insert(User.class, user);
//////		List<Dept> list = manager.execute("select * from dept", Dept.class, new HashMap());
////		System.out.println(list);
//		
//		
//	}
	
	@Test
	public void addUser() throws Exception {
		
		manager.genSQLTemplateToConsole("user");
		
		
	}
	

}