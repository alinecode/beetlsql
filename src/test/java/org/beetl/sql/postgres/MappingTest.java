package org.beetl.sql.postgres;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.beetl.sql.PostgresConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.oracle.OracleType;
import org.junit.Before;
import org.junit.Test;

public class MappingTest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/");
		manager = new SQLManager(new PostgresStyle(), loader, new PostgresConnectoinSource(),new  HumpNameConversion(),
				new Interceptor[]{new DebugInterceptor()});
	}

	

	//@Test
	public void addMap() throws Exception {
		
		Connection conn = manager.getDs().getMaster();
		DatabaseMetaData dbmd =  conn.getMetaData();
		
		/*
		ResultSet rs = dbmd.getTables(null, "%", "%",
				new String[] { "TABLE" });
		
		while(rs.next()){
			System.out.println(rs.getString(3));
		}
		*/
		
		
		MappingBean type = new MappingBean();
		type.setDate(new Date());
		type.setMoney(3.22);
		type.setPhoto(new byte[]{1,2,3});
		type.setText("hello,long text");
//		type.setTime(new Date());
		type.setTimestamp(new Timestamp(122323l));
		manager.insert(MappingBean.class, type);
		
		
		
	}
	
	@Test
	public void selectMap() throws Exception {
		List<MappingBean> list = manager.all(MappingBean.class);
		System.out.println(list);
	}
}
