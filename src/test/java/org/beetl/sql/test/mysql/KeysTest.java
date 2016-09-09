package org.beetl.sql.test.mysql;

import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.test.mysql.entity.MutipleKeys;
import org.beetl.sql.test.mysql.entity.User;
import org.beetl.xlsunit.RowHolderFacotoy;
import org.beetl.xlsunit.VariableTable;
import org.beetl.xlsunit.XLSParser;
import org.junit.Before;
import org.junit.Test;

public class KeysTest extends BaseMySqlTest {
	XLSParser keysParser = null;
	


	@Before
	public void init() {
		super.init();
		keysParser = new XLSParser(BaseMySqlTest.loader, "user/keys.xlsx", dbAccess,
				new RowHolderFacotoy.RowBeetlSQLHolderFactory());
	
	}


	
	@Test
	public void testUpate() {
		VariableTable vars = new VariableTable();
		keysParser.prepare("update", vars);
		keysParser.init(vars);
		String joel = vars.findString("paras.name1");
		MutipleKeys vo = new MutipleKeys();
		vo.setUserName(joel);
		//总是1
		vo.setGender(1);
		vo.setDescription(vars.findString("value.description"));
		
		sqlManager.updateById(vo);
		
		
		keysParser.test("update", vars);
		
		
	}
	
	
	@Test
	public void testDelete() {
		VariableTable vars = new VariableTable();
		keysParser.prepare("delete", vars);
		keysParser.init(vars);
		String joel = vars.findString("paras.name1");
		MutipleKeys vo = new MutipleKeys();
		vo.setUserName(joel);
		//总是1
		vo.setGender(1);
	
		sqlManager.deleteById(MutipleKeys.class, vo);
		keysParser.test("delete", vars);
		
		
	}
	
	
	
	
}
