package org.beetl.sql.test;

import java.io.Reader;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{
		
		
////		DB2SqlStyle style = new DB2SqlStyle();
		MySqlStyle style = new MySqlStyle();
////		OracleStyle style = new OracleStyle();
//		
//		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		
		ConnectionSource cs  = ConnectionSourceHelper.getSingle(datasource());
		
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		DebugInterceptor debug = new DebugInterceptor(QuickTest.class.getName());
	
				
		
		Interceptor[] inters = new Interceptor[]{ debug};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);
		
//		sql.execute("select count(1) from user", Long.class, null);
//		sql.execute(new SQLReady("select count(1) from user"), Long.class) ;
//		sql.select("wan.user.cc", User.class);
//		sql.genPojoCodeToConsole("user");
		Credit credit = sql.unique(Credit.class, 1);
		Reader rs = credit.getText().getCharacterStream();
		char[] cc = new char[128];
		int len = rs.read(cc);
		String kk = new String(cc,0,len);
		System.out.println(kk);
		
//		dao.templatePage(query);
		
//		System.out.println(page.getTotalRow());
//		System.out.println(page.getList());
//		System.out.println(page.getTotalPage());
		
//		List<Integer> ids = new ArrayList<Integer>();
//		ids.add(999);
//		dao.deleteByUserIds(ids);

			
	}
	
	
	public static User unique(SQLManager sql,Object key){
		return sql.unique(User.class, key);
	}
	
	public static DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(MysqlDBConfig.url);
		ds.setUsername(MysqlDBConfig.userName);
		ds.setPassword(MysqlDBConfig.password);
		ds.setDriverClassName(MysqlDBConfig.driver);
		return ds;
	}
	
	public static DataSource druidSource() {
		com.alibaba.druid.pool.DruidDataSource ds = new com.alibaba.druid.pool.DruidDataSource();
		ds.setUrl(MysqlDBConfig.url);
		ds.setUsername(MysqlDBConfig.userName);
		ds.setPassword(MysqlDBConfig.password);
		ds.setDriverClassName(MysqlDBConfig.driver);
		return ds;
	}
	
	
	
	
}


