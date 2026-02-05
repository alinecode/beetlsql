package org.beetl.sql.sugar;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;

import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试sql模版简化写法
 * @see ShortHolderFactory
 *
 */
public class ShortHolderTest  {

	static DataSource dataSource = datasource();
	private static   DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");

		return ds;
	}
	private  static SQLManager getSugarConfigSQLManager(){

		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		builder.setProduct(false);
		SQLManager sqlManager = builder.build();
		SugarTemplateConfig config = new SugarTemplateConfig();
		config.config(sqlManager);
		return sqlManager;
	}
	@Test
	public void testAnd(){

		SQLManager sqlManager = getSugarConfigSQLManager();
		SugarUser user = new SugarUser();
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


		user = new SugarUser();
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
		SQLManager sqlManager = getSugarConfigSQLManager();
		SugarUser user = new SugarUser();
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
		SQLManager sqlManager = getSugarConfigSQLManager();
		SugarUser user = new SugarUser();
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

		{
			String sqlTemplate = "update user set  id=#{id} #{age,set} where id=#{id}";
			String expectedJDBC = "update user set  id=?  where id=?";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,user);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

	}

	@Test
	public void testOrderBy(){
		SQLManager sqlManager = getSugarConfigSQLManager();
		Map map = new HashMap<>();
		map.put("orderBy","name");

		{
			String sqlTemplate = "select * from  user  ${orderBy,asc}";
			String expectedJDBC = "select * from  user  order by name asc";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,map);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

		map = new HashMap<>();
		map.put("orderBy",null);

		{
			String sqlTemplate = "select * from  user ${orderBy,asc}";
			String expectedJDBC = "select * from  user ";
			SQLResult sqlResult = sqlManager.getSQLResult(sqlTemplate,map);
			Assert.assertEquals(expectedJDBC,sqlResult.jdbcSql);
		}

	}

	@Data
	@Table(name="sys_user")
	public class SugarUser {
		@AutoID
		Integer id;
		String name;
		Integer age;
		Integer departmentId;
		Date createDate;
	}
}
