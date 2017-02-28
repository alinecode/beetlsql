package org.beetl.sql.test.mysql;


import org.beetl.ormunit.BeetlSQLDatabaseAccess;
import org.beetl.ormunit.BeetlSQLMapper;
import org.beetl.ormunit.XLSFileLoader;
import org.beetl.ormunit.XLSLoader;
import org.beetl.sql.core.IDAutoGen;
import org.beetl.sql.core.SQLManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-mysql-beetlsql.xml" })
@Transactional
public class BaseMySqlTest {
	
	protected BeetlSQLDatabaseAccess dbAccess = null;
	
	@Autowired
	protected SQLManager sqlManager;

	
	public static BeetlSQLMapper mapper  = null;
	public static XLSLoader loader = null;
	@BeforeClass
	public static void initData(){
		
		String root = System.getProperty("user.dir")+"/src/test/resources/xls";
		loader = new XLSFileLoader(root);
	}

	public void init() {
		//entity package
		String[] searchPath = new String[]{"org.beetl.sql.test.mysql.entity"};
		mapper = new  BeetlSQLMapper(sqlManager,searchPath);
		dbAccess = new BeetlSQLDatabaseAccess(sqlManager,mapper);
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
	
//	@Test
	public void testEnv() throws Exception{
		sqlManager.genPojoCodeToConsole("MUTIPLE_KEYS");
	
		
	}

	
	

}
