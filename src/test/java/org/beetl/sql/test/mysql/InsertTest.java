package org.beetl.sql.test.mysql;

import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.test.mysql.entity.Role;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;

import com.coamc.xlsunit.RowHolderFacotoy;
import com.coamc.xlsunit.VariableTable;
import com.coamc.xlsunit.XLSParser;

public class InsertTest extends BaseMySqlTest {
	XLSParser userParser = null;
	
	@Before
	public void init() {
		super.init();
		userParser = new XLSParser(loader, "user/insert.xlsx", dbAccess,
				new RowHolderFacotoy.RowBeetlSQLHolderFactory());	
	}

	
	@Test
	public void testInsert() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		String joel = vars.findString("name.joel");
		String lucy = vars.findString("name.lucy");
		User user = new User();
		user.setName(joel);
		user.setDepartmentId(1);
		sqlManager.insert(user, true);
		vars.add("userId1", user.getId());
		user = new User();
		user.setName(lucy);
		user.setDepartmentId(1);
		KeyHolder holder = new KeyHolder();
		sqlManager.insert(User.class, user, holder);
		vars.add("userId2", holder.getKey());
		userParser.test("insertUser", vars);
		
	}
	
	@Test
	public void testAssign() {
		Role role = new Role();
		role.setId(2);
		role.setName("ok");
		sqlManager.insert(Role.class,role);
		
		
	}
	
	
	
	
}
