package org.beetl.sql.test.mysql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)  
@Suite.SuiteClasses({   
	GeneralUserTest.class,   
	KeysTest.class,
	InsertTest.class,
	MapperTest.class,
	OrmTest.class,
	MapperMoreTest.class,
})  

public class AllMysqlTestSuit {

}
