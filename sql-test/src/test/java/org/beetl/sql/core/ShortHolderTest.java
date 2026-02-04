package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.Test;

public class ShortHolderTest extends BaseTest {
	@Test
	public void testAnd(){
		User user = new User();
		user.setId(1);

		{
			String sqlTemplate = "select * from user where #{id,or}";
			String expectedJDBC = "select * from user where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

		{
			String sqlTemplate = "select * from user where #{id,and}";
			String expectedJDBC = "select * from user where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

		{
			String sqlTemplate = "select * from user where id2=#{id} #{id,and}";
			String expectedJDBC = "select * from user where id2=? and id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

		{
			String sqlTemplate = "select * from user where #{id,and} #{id,or}";
			String expectedJDBC = "select * from user where id=? or id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}


		user = new User();
		user.setId(null);
		{
			String sqlTemplate = "select * from user where 1=1 #{id,and} #{id,or}";
			String expectedJDBC = "select * from user where 1=1";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql.trim());
		}


	}

	@Test
	public void testComplexAnd(){
		User user = new User();
		user.setId(1);

		{
			String sqlTemplate = "select * from user u where #{'u.id'==id,or}";
			String expectedJDBC = "select * from user u where u.id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

		{
			String sqlTemplate = "select * from user u where #{'u.id'>id,or}";
			String expectedJDBC = "select * from user u where u.id>?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

		{
			String sqlTemplate = "select * from user u where #{'u.id'<id+1,or}";
			String expectedJDBC = "select * from user u where u.id<?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcPara.get(0).value,2);
		}
	}

	@Test
	public void testUpdate(){
		User user = new User();
		user.setId(1);
		user.setName("hebeicaihua");

		{
			String sqlTemplate = "update user set  #{name,set} where id=#{id}";
			String expectedJDBC = "update user set  name=? where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}
		{
			String sqlTemplate = "update user set  #{name+'ok',set} where id=#{id}";
			String expectedJDBC = "update user set  name=? where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcPara.get(0).value,user.getName()+"ok");
		}

		{
			String sqlTemplate = "update user set  id=#{id} #{name,set} where id=#{id}";
			String expectedJDBC = "update user set  id=? , name=? where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

	}

	@Test
	public void testOrderBy(){
		User user = new User();
		user.setId(1);
		user.setName("hebeicaihua");

		{
			String sqlTemplate = "select   #{name,set} where id=#{id}";
			String expectedJDBC = "update user set  name=? where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}
		{
			String sqlTemplate = "update user set  #{name+'ok',set} where id=#{id}";
			String expectedJDBC = "update user set  name=? where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcPara.get(0).value,user.getName()+"ok");
		}

		{
			String sqlTemplate = "update user set  id=#{id} #{name,set} where id=#{id}";
			String expectedJDBC = "update user set  id=? , name=? where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

	}
}
