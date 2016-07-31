package org.beetl.sql.test;



import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;





public class QuickTest {
	
	public static void main(String[] args) throws Exception{

//		MySqlStyle style = new MySqlStyle();
//	
//		MySqlConnectoinSource cs = new MySqlConnectoinSource();
//		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
//		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});

		String sql = "    SELECT\r * from user";
		String type = getFirstToken(sql);
		System.out.println("type="+type);
	
	}
	private static String getFirstToken(String sql){
		boolean start = false;
		int startIndex = 0;
		for(int i=0;i<sql.length();i++){
			char c = sql.charAt(i);
			if(!start){
				if(!isSpecialChar(c)){
					start = true;
					startIndex = i;
					
				}
				continue;
			}
			
			if(isSpecialChar(c)){
				return sql.substring(startIndex,i).toLowerCase();
			}
			
		}
		return "";
	}
	
	private static boolean isSpecialChar(char c){
		return c==' '||c=='\t'||c=='\r'||c=='\n';
	}
	
}
