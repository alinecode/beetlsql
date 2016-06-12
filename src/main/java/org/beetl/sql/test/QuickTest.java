package org.beetl.sql.test;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.ext.DebugInterceptor;


public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		OracleStyle style = new OracleStyle();
	
		OracleConnectoinSource cs = new OracleConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		LijzTest key = new LijzTest();
//		key.setId1(1);
//		key.setId2(2);
//		LijzTest t = sql.unique(LijzTest.class, key);
//		t.setName("abc");
//		t.setId1(5);
//		sql.updateById(t);
		
		sql.all(LijzTest.class);
		
		
		
	}

	
}
