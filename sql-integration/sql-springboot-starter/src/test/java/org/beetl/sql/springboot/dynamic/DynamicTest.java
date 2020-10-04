package org.beetl.sql.springboot.dynamic;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.springboot.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DynamicApplication.class)
//@Transactional 多数据源事务spring没有内置实现
public class DynamicTest {
    @Autowired
    @Qualifier("sqlManager")
    SQLManager sqlManager;
    @Autowired
    DynamicUserInfoMapper mapper;
    @Test
    public void test(){
        mapper.deleteById(19999);
        sqlManager.single(UserInfoInDs1.class,1);
        sqlManager.single(UserInfoInDs2.class,1);
        mapper.single(1);
        UserInfoInDs2 userInfoInDs2 = mapper.queryById(1);
        System.out.println(userInfoInDs2.getId());
    }

    /*以下代码初始化数据库用*/
	@Autowired
	@Qualifier("sqlManager1")
	SQLManager sqlManager1;

	@Autowired
	@Qualifier("sqlManager1")
	SQLManager sqlManager2;


	@Before
	public void init(){
		DBInitHelper.executeSqlScript(sqlManager1,"db/schema.sql");
		DBInitHelper.executeSqlScript(sqlManager2,"db/schema.sql");
	}
}