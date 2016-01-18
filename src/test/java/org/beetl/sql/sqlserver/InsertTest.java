package org.beetl.sql.sqlserver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.beetl.sql.SqlServerConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.core.db.SqlServerStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * 
 * CREATE TABLE [dbo].[Test_lijz] (
[id] int  IDENTITY(1,1) ,
[name] varchar(1) COLLATE Chinese_PRC_CI_AS NULL ,
[create_time] datetime NULL ,
[age] decimal(5,2) NULL ,
CONSTRAINT [PK__Test_lij__3213E83F0BC6C43E] PRIMARY KEY ([id])
)
 * @author Administrator
 *
 */
public class InsertTest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		OracleStyle style = new OracleStyle();
		loader = new ClasspathLoader("/sql/",style);
		manager = new SQLManager(new SqlServerStyle(), loader, new SqlServerConnectoinSource(),new  UnderlinedNameConversion(),
				new Interceptor[]{new DebugInterceptor()});
	}

	

	@Test
	public void addUser() throws Exception {
		
//		Connection conn = manager.getDs().getMaster();
//		DatabaseMetaData dbmd =  conn.getMetaData();
//		
//		ResultSet rs = dbmd.getTables(null, "guest", "user",
//				new String[] { "TABLE" });
//		while(rs.next()) {
//		    System.out.println(rs.getString("TABLE_NAME"));
//		}
//		

//		manager.genPojoCodeToConsole("test_lijz");
//		manager.genSQLTemplateToConsole("test_lijz");
		manager.genPojoCodeToConsole("guest.user");
		manager.genSQLTemplateToConsole("guest.user");
		
//		TestLijz user = new TestLijz();
//		user.setName("ok");
//		user.setAge(1d);
//		user.setCreateTime(new Date());
//		manager.insert(user);
//		

		
	}
	
//	@Test
	public void query() throws Exception {
		

		TestLijz user = new TestLijz();
		user.setName("ok");
		user.setAge(1d);
		
//		manager.all(TestLijz.class);
		
//		manager.template(user);
		user = new TestLijz();
		manager.template(user,1,10);
	}
	
	

}