package org.beetl.sql.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.kit.MDParser;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * 
 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{
		InputStream ins = QuickTest.class.getResourceAsStream("/org/beetl/sql/test/user.md");
		BufferedReader   bf = new BufferedReader(new InputStreamReader(ins,"UTF-8"));
		MDParser parser = new MDParser("user",bf);
		SQLSource source = null;
		while((source=parser.next())!=null){
			System.out.println(source.getId());
			System.out.println(source.getLine());
			System.out.println(source.getTemplate());
			
		}
		
//		DB2SqlStyle style = new DB2SqlStyle();
		MySqlStyle style = new MySqlStyle();
//		OracleStyle style = new OracleStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		
		Interceptor[] inters = new Interceptor[]{ new DebugInterceptor()};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);
		
		UserDao dao = sql.getMapper(UserDao.class);
		dao.getIds3();
//		User user = dao.unique(163);
//		List list = (List)user.get("roles");
//		System.out.println(list);
//		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(
//				"datasteam.txt"));
//		
//		os.writeObject(user);
//		
//		ObjectInputStream ins = new ObjectInputStream(new FileInputStream(
//				"datasteam.txt"));
//		User newUser = (User)ins.readObject();
//		System.out.println(newUser.get("myDept"));
//		System.out.println(newUser.get("roles"));
		
	
			
	}
	
	
}


