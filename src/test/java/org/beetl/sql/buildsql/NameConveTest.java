package org.beetl.sql.buildsql;

import javax.persistence.Column;
import javax.persistence.Table;

import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.junit.Before;
import org.junit.Test;


public class NameConveTest {
	
	NameConversion hnc;
	NameConversion unc;

	@Before
	public void setUp() throws Exception {
		hnc = new HumpNameConversion();
		unc = new UnderlinedNameConversion();
	}

//	@Test
//	public void humpNameConversion() {
//		
//		Assert.assertEquals(hnc.getClassName("user"), "User");
//		Assert.assertEquals(hnc.getClassName("tUser"), "TUser");
//		Assert.assertEquals(hnc.getClassName("TUser"), "TUser");
//		
//		Assert.assertEquals(hnc.getTableName(User.class.getSimpleName()), "user");
//		
//		Assert.assertEquals(hnc.getColName("name"), "name");
//		Assert.assertEquals(hnc.getColName("Name"), "name");
//		Assert.assertEquals(hnc.getColName("tName"), "tName");
//		Assert.assertEquals(hnc.getColName("TName"), "tName");
//		
//		Assert.assertEquals(hnc.getPropertyName("name"), "name");
//		Assert.assertEquals(hnc.getPropertyName("tName"), "tName");
//		Assert.assertEquals(hnc.getPropertyName("TName"), "tName");
//		
////		Assert.assertEquals(hnc.getId().toString(), "");
//		
//	}
	
//	@Test
//	public void underlinedNameConversion() {
//		
//		Assert.assertEquals(unc.getClassName("user"), "User");
//		Assert.assertEquals(unc.getClassName("t_user"), "TUser");
//		Assert.assertEquals(unc.getClassName("t_user_a"), "TUserA");
//		
//		Assert.assertEquals(unc.getTableName(User.class.getSimpleName()), "user");
//		
//		Assert.assertEquals(unc.getColName("userName"), "user_name");
//		Assert.assertEquals(unc.getColName("UserName"), "user_name");
//		
//		Assert.assertEquals(unc.getPropertyName("user_name"), "userName");
//		Assert.assertEquals(unc.getPropertyName("User_Name"), "userName");
//		Assert.assertEquals(unc.getPropertyName("USER_NAME"), "userName");
//		
////		Assert.assertEquals(unc.getId().toString(), "");
//	
//	}
	
	@Test
	public void testJPANameConversion(){
		
	}
	
	

}
