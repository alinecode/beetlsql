package org.beetl.sql.test;



import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;



/**
 * CREATE TABLE `user` (
  `USER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `age` int(11) DEFAULT NULL COMMENT '年纪123',
  `bir` datetime DEFAULT NULL COMMENT '生日',
  `user_name` varchar(255) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE `department` (
  `id` int(11) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


 * @author Administrator
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
		
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	sql = new SQLManager(style,loader,cs,new DarrenNameConversion(), new Interceptor[]{new DebugInterceptor()});
		
//		sql.genPojoCodeToConsole("Sys_User");
//		sql.unique(SysUser.class, 1);
		SysUser user = new SysUser();
		user.setName("c");
		user.setUserAge(2);
//		sql.insert(user);
//		
		sql.template(user);
		
//		Party party = new Party();
//		party.setId1(1);
//		party.setId2(1);
//		sql.unique(Party.class, party);
//		sql.genPojoCodeToConsole("party");
	}
	
	
}
