package org.beetl.sql.test.mysql;

import java.util.List;

import org.beetl.sql.test.mysql.dao.UserDao;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coamc.xlsunit.RowHolderFacotoy;
import com.coamc.xlsunit.VariableTable;
import com.coamc.xlsunit.XLSParser;

public class MapperTest extends BaseMySqlTest {
	
	@Autowired
	UserDao userDao;
	
	XLSParser userParser = null;

	
	@Before
	public void init() {
		super.init();
		userParser = new XLSParser(loader, "user/general.xlsx", dbAccess,
				new RowHolderFacotoy.RowBeetlSQLHolderFactory());	
	}


	
	@Test
	public void testUnique() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		int id = vars.findInteger("userId1");
		
		User joel = userDao.unique(id);
		User user = userDao.single(id);
		org.junit.Assert.assertEquals(joel.getName(), user.getName());
		User nullUser = userDao.single(-1);
		org.junit.Assert.assertNull(nullUser);
		try{
			userDao.unique(-1);
			org.junit.Assert.fail();
		}catch(Exception ex){
			//should go here
		}
	}
	
	@Test
	public void testCase1() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		String joel  = vars.findString("name.joel");
		String lucy  = vars.findString("name.lucy");
		String[] names = {joel,lucy};
		List<User> list = userDao.selectUsers(names);
		User user = list.get(0);
		org.junit.Assert.assertEquals(joel, user.getName());
	}
	
	
	
	
	
}
