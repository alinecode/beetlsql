package org.beetl.sql.test;

import java.util.List;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;


public class QuickTest {
	
	public static void main(String[] args) throws Exception{

		MySqlStyle style = new MySqlStyle();
	
		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), new Interceptor[]{new DebugInterceptor()});
//		Party key = new Party();
//		key.setId1(1);
//		key.setId2(2);
//		Party party = sql.unique(Party.class, key);
//		party.setName("anc");
//		sql.deleteById(Party.class, key);
		
//		Party newParty = new Party();
//		newParty.setId1(1);
//		newParty.setId2(2);
//		newParty.setName("gf");
//		sql.template(newParty);
//		sql.updateTemplateById(newParty);
		User user = sql.unique(User.class, 2);
		user.setName("hello");
		sql.updateTemplateById(user);
	}

	
}
