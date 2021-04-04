package org.beetl.sql.core.mapper;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.entity.User;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.mapper.annotation.SubQuery;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * 验证LambdaQuery增强版本
 */
public class SubQueryCommonTest extends BaseTest {

	@BeforeClass
	public static void init(){
		initTable(testSqlFile);
	}

	@Test
    public void commonLambda(){
		AnyMapper anyMapper = sqlManager.getMapper(AnyMapper.class);
		LambdaQuery<User> lambdaQuery = anyMapper.allUserInDepartment(1);
		List<User> list= lambdaQuery.select();
		Assert.assertEquals(1,list.size());

		List<User> newList = lambdaQuery.andEq(User::getAge,42).select();
		Assert.assertEquals(0,newList.size());
    }


    @SqlResource("lambda")
    public static interface AnyMapper extends BaseMapper<User>{
    	/* 构造一个公共的子查询Lambda,lambda#allUserInDepartment构成
    	*/
    	@SubQuery
    	public LambdaQuery<User> allUserInDepartment(Integer deptId);
	}


}

