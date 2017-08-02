package org.beetl.sql.test.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.test.mysql.entity.Department;
import org.beetl.sql.test.mysql.entity.ProductOrder;
import org.beetl.sql.test.mysql.entity.Role;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;

import com.coamc.xlsunit.RowHolderFacotoy;
import com.coamc.xlsunit.VariableTable;
import com.coamc.xlsunit.XLSParser;

public class OrmTest extends BaseMySqlTest {
	XLSParser userParser = null;
	
	@Before
	public void init() {
		super.init();
		userParser = new XLSParser(loader, "user/orm.xlsx", dbAccess,
				new RowHolderFacotoy.RowBeetlSQLHolderFactory());	
	}


	@Test
	public void testOrmQuery() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);		
	
		PageQuery query2 = new PageQuery();
		//query hank li and joel li
		Map map = new HashMap();
		map.put("name", "li");
		query2.setParas(map);
		query2.setPageNumber(1);
		sqlManager.pageQuery("user.selectOrmUser", User.class, query2);
		List<User> list2 = (List<User>)query2.getList();
		User joel = list2.get(0);
		Department dept = (Department)joel.get("department");
		String expectedDeptNamer = vars.findString("name.dept");
		org.junit.Assert.assertEquals(expectedDeptNamer, dept.getName());
		List<Role> roles = (List<Role>)joel.get("role");
		Role role = roles.get(0);
		String expectedRoleName = vars.findString("role.emp");
		org.junit.Assert.assertEquals(expectedRoleName, role.getName());
		
		List<ProductOrder> orders =(List<ProductOrder>) joel.get("productOrder");
		//joel 订购了俩个产品
		org.junit.Assert.assertEquals(2, orders.size());	
			
	}
	
	
	
	@Test
	public void testLazyOrmQuery() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);		
	
		PageQuery query2 = new PageQuery();
		//query hank li and joel li
		Map map = new HashMap();
		map.put("name", "li");
		query2.setParas(map);
		query2.setPageNumber(1);
		sqlManager.pageQuery("user.selectLazyOrmUser", User.class, query2);
		List<User> list2 = (List<User>)query2.getList();
		User joel = list2.get(0);
		Department dept = (Department)joel.get("department");
		String expectedDeptNamer = vars.findString("name.dept");
		org.junit.Assert.assertEquals(expectedDeptNamer, dept.getName());
		List<Role> roles = (List<Role>)joel.get("role");
		Role role = roles.get(0);
		String expectedRoleName = vars.findString("role.emp");
		org.junit.Assert.assertEquals(expectedRoleName, role.getName());
		
		List<ProductOrder> orders =(List<ProductOrder>) joel.get("productOrder");
		//joel 订购了俩个产品
		org.junit.Assert.assertEquals(2, orders.size());	
			
	}
	
	
	
	
	
	
	
}
