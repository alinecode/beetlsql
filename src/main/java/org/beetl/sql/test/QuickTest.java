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

	public static void main(String[] args) {
		MySqlStyle style = new MySqlStyle();
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/sql");
		SQLManager sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		sql.unique(SysRole.class, 1);
//		sql.all(SysRole.class);
//		sql.allCount(SysRole.class);
//		sql.deleteById(SysRole.class, 10000);
		{
//			SysRole role = new SysRole();
//			role.setName("aaabb");
//			role.setCompanyId(1);
////			sql.insert(SysRole.class, role);
//			KeyHolder kh = new KeyHolder();
//			sql.insert(SysRole.class, role, kh);
//			System.out.println(kh.getKey());
			
		}
		{
//			SysRole role = new SysRole();
//			role.setName("aaabb");
//			role.setCompanyId(1);
//			sql.template(role);
			
		}
		{
//			SysRole role = new SysRole();
//			role.setId(1);
//			role.setName("admin");
//			sql.updateById(role);
		
		}
		
		

	}

}
