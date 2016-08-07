package org.beetl.sql.test;



import java.util.Random;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.IDAutoGen;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;





public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		sql.addIdAutonGen("uuid2", new IDAutoGen(){
//
//			@Override
//			public Object nextID(String params) {
//				return "hi"+new Random().nextInt(10000);
//			}
//			
//		});
		//		String table = "party"; 
		sql.genPojoCodeToConsole("user");
//		sql.genSQLTemplateToConsole(table);
//		Party party = new Party();
//		party.setName("party");
//		party.setId("abc123");
//		sql.insert(party);
	
	}
	
	
}
