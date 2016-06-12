package org.beetl.sql.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.Params;
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
		
		List<Map> ret =sql.select("user.findById", HashMap.class, Params.ins().add("id", 2).map());
		System.out.println(ret.get(0));
		
		//		Party key = new Party();
//		key.setId1(1);
//		key.setId2(2);
//		Party party = sql.unique(Party.class, 1);
//		party.setName("anc");
//		sql.deleteById(Party.class, key);
		
//		Party newParty = new Party();
//		newParty.setId1(1);
//		newParty.setId2(2);
//		newParty.setName("gf");
//		sql.template(newParty);
//		sql.updateTemplateById(newParty);
//		boolean sucess = sql.executeOnConnection(new OnConnection<Boolean>(){
//
//			@Override
//			public Boolean call(Connection conn) throws SQLException {
//				PreparedStatement ps = conn.prepareStatement("select * from user");
//				ResultSet rs = ps.executeQuery();
//				while(rs.next()){
//					System.out.println(rs.getString("name"));
//					
//				}
//				rs.close();
//				return true;
//			}
//			
//		});
	}

	
}
