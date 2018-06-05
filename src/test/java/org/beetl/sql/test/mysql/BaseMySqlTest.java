package org.beetl.sql.test.mysql;


import org.beetl.sql.core.IDAutoGen;
import org.beetl.sql.core.SQLManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.coamc.xlsunit.BeetlSqlDBAccess;
import com.coamc.xlsunit.DBAccess;
import com.coamc.xlsunit.XLSFileLoader;
import com.coamc.xlsunit.XLSLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-mysql-beetlsql.xml" })
@Transactional
public class BaseMySqlTest {
	
	
	@Autowired
	protected SQLManager sqlManager;

	
	public  XLSLoader loader = null;
	
	DBAccess dbAccess = null;
	@Before
	public  void init(){
		
		String root = System.getProperty("user.dir")+"/src/test/resources/xls";
		loader = new XLSFileLoader(root);
		
		dbAccess = new BeetlSqlDBAccess(sqlManager);
		
		sqlManager.addIdAutonGen("uuidSample", new IDAutoGen(){
			public  long seq = System.currentTimeMillis();
			@Override
			public Object nextID(String params) {
				synchronized(this){
					seq= seq+1;
					return seq+"";
				}
				
			}
			
		});
		
		
		
	}


	@Test //用来测试配置是否正确
	public void testEnv() throws Exception{
		sqlManager.genPojoCodeToConsole("user","com.test");
	
		
	}

	
	

}
