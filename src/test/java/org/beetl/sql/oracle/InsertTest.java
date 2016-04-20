package org.beetl.sql.oracle;

import java.util.Date;

import org.beetl.sql.OracleConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.pojo.User;
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
		
		manager.genPojoCodeToConsole("MESSAGE_HEAD");
		MessageHead h = new MessageHead();
		h.setFunctionCode("aa");
		manager.insert(h,true);
		System.out.println(h.getHeadId());
		
//		MyUser user = new MyUser();
//		user.setName("lijz");
//		user.setAge(1);
//		user.setBir(new Date());
//		manager.insert(MyUser.class,user,new KeyHolder());
//		System.out.println(user.getId());
		
		
		

	}
	
	
//	@Test
//	public void addBatchUser() throws Exception {
//		
//		MyUser user = new MyUser();
//		user.setName("lijz");
//		user.setAge(1);
//		List list = new ArrayList();
//		
//		list.add(user);
//		list.add(user);
//		manager.insertBatch(MyUser.class, list);
//		
//		
//		
//
//	}
//	
	
	
	

}