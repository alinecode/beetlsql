package org.beetl.sql.test.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;

import com.coamc.xlsunit.RowHolderFacotoy;
import com.coamc.xlsunit.VariableTable;
import com.coamc.xlsunit.XLSParser;

public class GeneralUserTest extends BaseMySqlTest {
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
		String expected = vars.findString("name.joel");
		User joel = sqlManager.unique(User.class, id);
		org.junit.Assert.assertEquals(expected, joel.getName());
		
	}
	
	@Test
	public void testSingle() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		int id = vars.findInteger("userId1");
		String expected = vars.findString("name.joel");
		User joel = sqlManager.single(User.class, id);
		org.junit.Assert.assertEquals(expected, joel.getName());
		
	}
	
	@Test
	public void testTemplateQuery() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		int id = vars.findInteger("userId1");
		String expected = vars.findString("name.joel");
		User template = new User();
		template.setId(id);
		List<User> list = sqlManager.template(template);
		org.junit.Assert.assertEquals(1,list.size());
		User joel = list.get(0);
		org.junit.Assert.assertEquals(expected, joel.getName());
		
	}
	
	
	@Test
	public void testAllQuery() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);		
		List<User> list = sqlManager.all(User.class);
		org.junit.Assert.assertEquals(3,list.size());
			
	}
	
	
	@Test
	public void testPageQuery1() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);		
		List<User> list = sqlManager.all(User.class, 1, 3);
		org.junit.Assert.assertEquals(3,list.size());
			
	}
	
	@Test
	public void testPageQuery2() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);		
		List<User> list = sqlManager.all(User.class, 2, 3);
		org.junit.Assert.assertEquals(2,list.size());
			
	}
	

	
	@Test
	public void testPageQuery3() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);		
	
		PageQuery query2 = new PageQuery();
		query2.setPageNumber(1);
		sqlManager.pageQuery("user.selectUser", User.class, query2);
		List<User> list2 = (List<User>)query2.getList();
		org.junit.Assert.assertEquals(3,list2.size());
		org.junit.Assert.assertEquals(1,query2.getTotalPage());
		org.junit.Assert.assertEquals(3,query2.getTotalRow());
		
			
	}
	
	
	@Test
	public void testPageQuery4() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);		
	
		PageQuery query2 = new PageQuery();
		//query hank li and joel li
		Map map = new HashMap();
		map.put("name", "li");
		query2.setParas(map);
		query2.setPageNumber(1);
		sqlManager.pageQuery("user.selectUser", User.class, query2);
		List<User> list2 = (List<User>)query2.getList();
		org.junit.Assert.assertEquals(2,list2.size());
		org.junit.Assert.assertEquals(1,query2.getTotalPage());
		org.junit.Assert.assertEquals(2,query2.getTotalRow());
			
	}
	
	@Test
	public void testTail() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		int id = vars.findInteger("userId1");
		String expected = vars.findString("name.dept");
		
		User query = new User();
		query.setId(id);
		List<User> users = sqlManager.select("user.selectSingleUser", User.class, query);
		org.junit.Assert.assertEquals(1,users.size());
		User joel = users.get(0);
		
		org.junit.Assert.assertEquals(expected,joel.get("deptName"));
	}
	
	
	//============== insert&update===================
	

	@Test
	public void testInsert() {
		VariableTable vars = new VariableTable();
		userParser.prepare("insert", vars);
		userParser.init(vars);
		int id = vars.findInteger("userId3");
		User user = new User();
		user.setId(id);
		user.setName(vars.findString("user.newName"));
		user.setDepartmentId(vars.findInteger("departmentId"));
		sqlManager.insert(user, true);
		
		vars.add("seq", user.getId());
		userParser.test("insert", vars);
		
	}
	
	
	@Test
	public void testUpdate() {
		VariableTable vars = new VariableTable();
		userParser.prepare("update", vars);
		userParser.init(vars);
		int id = vars.findInteger("userId3");
		
		String name = vars.findString("user.newName");
		
		User user = new User();
		user.setName(name);
		user.setId(id);
		user.setDepartmentId(vars.findInteger("departmentId"));
		
		sqlManager.updateById(user);
		userParser.test("update", vars);
		
	}
	
	
	@Test
	public void testDelete() {
		VariableTable vars = new VariableTable();
		userParser.prepare("delete", vars);
		userParser.init(vars);
		int id = vars.findInteger("userId3");
		sqlManager.deleteById(User.class, id);
		userParser.test("delete", vars);
		
		int size = sqlManager.all(User.class).size();
		org.junit.Assert.assertEquals(2,2);
		
	}
	
	
	
	
	
	
}
