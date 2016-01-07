package org.beetl.sql.test;

import java.util.Date;
import java.util.List;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

public class QuickTest {

	public static void main(String[] args) throws Exception{
		MySqlStyle style = new MySqlStyle();
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/sql");
//		SQLManager sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
		
//		sql.select("appliction.findAllByPage", SysRole.class, new HashMap());
		
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
		
		{
//			List<SysRole> list = sql.all(SysRole.class, 1, 9);
//			System.out.println(list.size());
//			
		}
		
//		sql.genSQLTemplate(MysqlDBConfig.class);
		
		SQLManager 	sql = new SQLManager(style,loader,cs,new DefaultNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		User info = new User();
//		info.setName("aa");
//		info.setMaxDate(new Date());
//		info.setMinDate(new Date());
//		List<User> list = sql.template(info);
		
//		sql.unique(User.class, 1);
		
		
//		info.setId(2);
//		info.setUserName("kk");
//		info.setAge(1);
//		
//		sql.updateTemplateById(info);
//		sql.all(User.class, 1, 2);
//		sql.select("user.selectAll",User.class, new HashMap(),1,3);
//		sql.genSQLTemplateToConsole("user");
//		sql.genALL("com.test", new GenConfig(), new GenFilter(){
//			public boolean accept(String tableName){
//				if(tableName.equalsIgnoreCase("user")){
//					return true;
//				}else{
//					return false;
//				}
//			}
//		});
//		Map map = new HashMap();
//		map.put("id", 2);
//		map.put("userName", "kkg");
//		sql.updateTemplateById(User.class, map);
		
		
		
//		sql.unique(UserInfo.class, 1);
//		UserInfo info = new UserInfo();
//		info.setUserName("name");
//		sql.template(info);
		

//		sql.genPojoCodeToConsole("user");
//		sql.genSQLTemplateToConsole("user");

//		sql.genPojoCodeToConsole("MyUserRole");
		sql.genSQLTemplateToConsole("user");

//		GenConfig config = new GenConfig();
//		config.preferBigDecimal(true);
////		config.setBaseClass("com.test.User");
//		sql.genPojoCode("sys_role","com.test",config);

	}

}
