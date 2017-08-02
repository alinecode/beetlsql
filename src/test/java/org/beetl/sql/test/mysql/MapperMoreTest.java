package org.beetl.sql.test.mysql;

import java.util.Date;
import java.util.Map;

import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.test.mysql.dao.UserTestDao;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coamc.xlsunit.RowHolderFacotoy;
import com.coamc.xlsunit.VariableTable;
import com.coamc.xlsunit.XLSParser;
/**
 * 测试
 * @author xiandafu
 *
 */
public class MapperMoreTest extends BaseMySqlTest {
	
	@Autowired
	UserTestDao userTestDao;
	
	XLSParser userParser = null;

	
	@Before
	public void init() {
		super.init();
		userParser = new XLSParser(loader, "user/moreMapper.xlsx", dbAccess,
				new RowHolderFacotoy.RowBeetlSQLHolderFactory());	
		PageQuery.DEFAULT_PAGE_SIZE = 10;
	}

	

	
	@Test
	public void generalQuery() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		
		int total = vars.findInteger("info.total");
		int totalLucy = vars.findInteger("info.totalLucy");
		int totalJoel = vars.findInteger("info.totalJoel");
	
		int realTotalJoel =0;
		if(JavaType.isJdk8()){
			realTotalJoel = userTestDao.query("joel").size();
			org.junit.Assert.assertEquals(totalJoel, realTotalJoel);
		}
		
		User template = new User();
		template.setName("joel");
		realTotalJoel = userTestDao.query(template).size();
		org.junit.Assert.assertEquals(totalJoel, realTotalJoel);
		
		
		realTotalJoel = userTestDao.query2("joel").size();
		org.junit.Assert.assertEquals(totalJoel, realTotalJoel);
	
		realTotalJoel = userTestDao.query2(template).size();
		org.junit.Assert.assertEquals(totalJoel, realTotalJoel);
		
		realTotalJoel = userTestDao.query3("joel").size();
		org.junit.Assert.assertEquals(totalJoel, realTotalJoel);
		
		
		
	}
	
	
	@Test
	public void mapping() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		
		
	
		Object o  = userTestDao.getIds2().get(0);
		if(!(o instanceof Long)){
			org.junit.Assert.fail();
		}
		
		o = userTestDao.getIdNames().get(0);
		if(!(o instanceof Map)){
			org.junit.Assert.fail();
		}
		
		int lastId = vars.findInteger("userId15");
		Map map = userTestDao.getUserInfo((long)lastId);
		
		
	
		
		
	}
	
	@Test
	public void generalRangeQuery() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		
		int total = vars.findInteger("info.total");
		int totalLucy = vars.findInteger("info.totalLucy");
		int totalJoel = vars.findInteger("info.totalJoel");
		int realTotalJoel = 0;
		if(JavaType.isJdk8()){
			realTotalJoel = userTestDao.query("joel", 1, 5).size();
			org.junit.Assert.assertEquals(5, realTotalJoel);
			
			realTotalJoel = userTestDao.query2(1, 5,"joel").size();
			org.junit.Assert.assertEquals(5, realTotalJoel);
		}
		
		
		 realTotalJoel = userTestDao.query3("joel", 1, 5).size();
		 org.junit.Assert.assertEquals(5, realTotalJoel);
		
		
	}
	
	@Test
	public void pageQuery() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		
		int total = vars.findInteger("info.total");
		int totalLucy = vars.findInteger("info.totalLucy");
		int totalJoel = vars.findInteger("info.totalJoel");
		PageQuery query = new PageQuery();
		query =  userTestDao.selectUser(query);
		//俩页
		org.junit.Assert.assertEquals(2, query.getTotalPage());
		if(JavaType.isJdk8()){
			query = userTestDao.selectUser(1, 5, "joel");
			org.junit.Assert.assertEquals(2, query.getTotalPage());
			query = new PageQuery();
			userTestDao.selectUser(query, "joel");
			org.junit.Assert.assertEquals(1, query.getTotalPage());
			
		}
		query = new PageQuery();
		userTestDao.selectUser2(query, "joel");
		org.junit.Assert.assertEquals(1, query.getTotalPage());
		
		//jdbc query
		if(JavaType.isJdk8()){
			query = userTestDao.jdbcQuery(1, 5, "%joel%");
			org.junit.Assert.assertEquals(2, query.getTotalPage());
		}
		
		query = userTestDao.jdbcQuery2(1, 5, "%joel%");
		org.junit.Assert.assertEquals(2, query.getTotalPage());

		
		
		
	}
	
	
	@Test
	public void insert() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		
		int total = vars.findInteger("info.total");
		int totalLucy = vars.findInteger("info.totalLucy");
		int totalJoel = vars.findInteger("info.totalJoel");
		int lastId = vars.findInteger("userId15");
		KeyHolder key = null;
		
		User user = null;
		user = new User();
		
		user.setName("joel");
		 key = userTestDao.insertUser(user);
		if(key.getInt()<lastId){
			org.junit.Assert.fail();
		}
		
		long newTotal = userTestDao.allCount();
		org.junit.Assert.assertEquals(total+1, newTotal);
		total = (int)newTotal;
		if(JavaType.isJdk8()){
			 key = userTestDao.insertUser("joel");
			 newTotal = userTestDao.allCount();
			 org.junit.Assert.assertEquals(total+1, newTotal);
			 total = (int)newTotal;
		}
		
		userTestDao.insertUser("jeoli",1);
		newTotal = userTestDao.allCount();
		org.junit.Assert.assertEquals(total+1, newTotal);
		total = (int)newTotal;
		
		userTestDao.insertUser2("jeoli");
		newTotal = userTestDao.allCount();
		org.junit.Assert.assertEquals(total+1, newTotal);
		total = (int)newTotal;
	
		
	}
	
	
	@Test
	public void update1() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		
		int id = vars.findInteger("userId1");
		User user = new User();
		user.setName("xdf");
		user.setId(id);
		userTestDao.updateUser(user);
		
		if(JavaType.isJdk8()){
			//excel里没有比较日期，这里只是验证日期字段被处理
			userTestDao.updateUser(user, new Date());
		}
		//使用xlsunit 自动比较
		userParser.test("update", vars);
		
		
			
	}
	
	@Test
	public void update2() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		int id = vars.findInteger("userId1");
		if(JavaType.isJdk8()){
			userTestDao.updateUser("xdf", id);
			userParser.test("update", vars);
			
			
		}
			
	}
	
	@Test
	public void update3() {
		VariableTable vars = new VariableTable();
		userParser.init(vars);
		int id = vars.findInteger("userId1");
		userTestDao.updateUser2("xdf", id);		
		userTestDao.updateUser3("xdf", id);
		userParser.test("update", vars);
	}
	
}
