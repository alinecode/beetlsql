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
		AppScanRecordMapper dao = sql.getMapper(AppScanRecordMapper.class);
		AppScanRecord r = new AppScanRecord();
		r.setLatitude("12");
		dao.insert(r, true);
		System.out.println(r.getId());
		
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
