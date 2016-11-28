package org.beetl.sql.test;



import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.SimpleCacheInterceptor;



/**
 * 

 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		List<String> lcs = new ArrayList<String>();
		lcs.add("user");
		SimpleCacheInterceptor cache =new SimpleCacheInterceptor(lcs);
		Interceptor[] inters = new Interceptor[]{ new DebugInterceptor(),cache};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);

		for(int i=0;i<2;i++){
			sql.unique(User.class, 1);
		}
	}
	
	
}
