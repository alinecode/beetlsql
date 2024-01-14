package org.beetl.sql.core;

import lombok.Data;
import lombok.experimental.Accessors;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * 测试不严格遵守JavaBean
 */
public class LombokTest  extends BaseTest {
	@BeforeClass
	public static void init() {
		SQLManager.javabeanStrict(false);
		initTable(testSqlFile);
	}

	@Test
	public void testBadBean(){
		BadBeanMapper badBeanMapper = sqlManager.getMapper(BadBeanMapper.class);

		BadBean badBean = badBeanMapper.unique(1);
		Assert.assertEquals("lijz",badBean.getName());
		badBean.setAge(15).setDepartmentId(23);
		badBeanMapper.updateById(badBean);
		badBean = badBeanMapper.unique(1);
		Assert.assertEquals(23,badBean.getDepartmentId().intValue());

		List<BadBean> badBeanList = badBeanMapper.select(23);
		Assert.assertEquals(1,badBeanList.size());

	}

	@Test
	public void testBadBean2(){
		BadBeanMapper2 badBeanMapper = sqlManager.getMapper(BadBeanMapper2.class);

		BadBean2 badBean = badBeanMapper.unique(1);
		Assert.assertEquals("lijz",badBean.getName());
		badBean.setAge(18);
		badBean.setDeptId(15);
		badBeanMapper.updateById(badBean);
		badBean = badBeanMapper.unique(1);
		Assert.assertEquals(15,badBean.getDeptId().intValue());

		List<BadBean2> badBeanList = badBeanMapper.select(15);
		Assert.assertEquals(1,badBeanList.size());

	}

	@Test
	public void testLambda(){
		BadBeanMapper2 badBeanMapper = sqlManager.getMapper(BadBeanMapper2.class);
		LambdaQuery<BadBean2> lambdaQuery = sqlManager.lambdaQuery(BadBean2.class);
		BadBean2 badBean2 = lambdaQuery.andEq(BadBean2::getAge,12).single();
		Assert.assertEquals(12,badBean2.getAge().intValue());

	}

	static interface  BadBeanMapper extends BaseMapper<BadBean>{
		@Sql("select * from sys_user where department_id=?")
		List<BadBean> select(Integer deptId) ;
	}

	static interface  BadBeanMapper2 extends BaseMapper<BadBean2>{
		@Sql("select * from sys_user where department_id=?")
		List<BadBean2> select(Integer deptId) ;
	}


	@Data
	@Accessors(chain = true)
	@Table(name="sys_user")
	static public class BadBean {
		@AutoID
		Integer id;
		String name;
		Integer age;
		Integer departmentId;
		Date createDate;
	}

	@Data
	@Accessors(chain = true)
	@Table(name="sys_user")
	static public class BadBean2 {
		@AutoID
		@Column("id")
		Integer ID; //应该是Id
		String Name;
		Integer Age;
		@Column("department_id")
		Integer DeptId;
		Date CreateDate;
	}

}
