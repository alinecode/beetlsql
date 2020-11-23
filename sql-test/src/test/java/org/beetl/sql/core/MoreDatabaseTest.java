package org.beetl.sql.core;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.TargetSQLManager;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证不同数据库使用同一个sqlManager或者Mapper
 */
public class MoreDatabaseTest  extends BaseTest {

	@Test
	public void conditional() {

		SQLManager a = getOne("a");
		SQLManager b = getOne("b");
		Map<String, SQLManager> map = new HashMap<>();
		map.put("a", a);
		map.put("b", b);
		SQLManager sqlManager = new ConditionalSQLManager(a, map);


		UserData user = new UserData();
		user.setName("hello");
		user.setDepartmentId(2);
		sqlManager.insert(user);

		DepartmentData dept = new DepartmentData();
		dept.setName("dept");
		sqlManager.insert(dept);

		//不同用户，用不同sqlManager操作，存入不同的数据库
		UserData userDb = a.single(UserData.class,user.getId());
		Assert.assertNotNull(userDb);

		DepartmentData deptDb = b.single(DepartmentData.class,dept.getId());
		Assert.assertNotNull(deptDb);

	}


	@Test
	public void conditionalMapperTest() {

		SQLManager a = getOne("a");
		SQLManager b = getOne("b");
		Map<String, SQLManager> map = new HashMap<>();
		map.put("a", a);
		map.put("b", b);
		SQLManager sqlManager = new ConditionalSQLManager(a, map);
		UserMapper mapper = sqlManager.getMapper(UserMapper.class);

		UserData user = new UserData();
		user.setName("hello");
		user.setDepartmentId(2);
		a.insert(user);

		DepartmentData dept = new DepartmentData();
		dept.setName("dept");
		b.insert(dept);

		UserData dbUser = mapper.getOne(user.getId());
		Assert.assertNotNull(dbUser);

		DepartmentData dbDept = mapper.getOneDepartment(dept.getId());
		Assert.assertNotNull(dbDept);

	}

	static interface UserMapper extends BaseMapper<UserData>{
		@Sql("select * from sys_user where id=?")
		UserData getOne(Integer id);

		@Sql("select * from department where id=?")
		DepartmentData getOneDepartment(Integer id);
	}



	/**
	 * 用户数据使用"a" sqlmanager
	 */
	@Data
	@Table(name = "sys_user")
	@TargetSQLManager("a")
	public static class UserData {
		@Auto
		private Integer id;
		private String name;
		private Integer departmentId;
	}

	/**
	 * 部门数据使用"b" sqlmanager
	 */
	@Data
	@Table(name = "department")
	@TargetSQLManager("b")
	public static class DepartmentData {
		@Auto
		private Integer id;
		private String name;
	}


	private  static   HikariDataSource newDatasource(String name) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:h2:mem:"+name+";DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setDriverClassName("org.h2.Driver");
		return dataSource;


	}

	private static SQLManager getOne(String dbName){
		DataSource ds = newDatasource(dbName);
		ConnectionSource source = ConnectionSourceHelper.getSingle(ds);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		SQLManager sqlManager = builder.build();
		initData(ds,testSqlFile);
		return sqlManager;

	}



	 static void initOneData(DataSource ds,String file)  {
		BaseTest.initData(ds,file);
	}


}
