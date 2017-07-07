package org.beetl.sql.test;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.PackagePathIdNameConversion;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author xiandafu
 *
 */

public class QuickTest {
	
	public static void main(String[] args) throws Exception{
		
		
		
//		DB2SqlStyle style = new DB2SqlStyle();
		MySqlStyle style = new MySqlStyle();
//		OracleStyle style = new OracleStyle();
		
//		MySqlConnectoinSource cs = new MySqlConnectoinSource();
		
		ConnectionSource cs  = ConnectionSourceHelper.getSingle(druidSource());
		
		SQLLoader loader = new ClasspathLoader("");
		DebugInterceptor debug = new DebugInterceptor(QuickTest.class.getName());
			
		
		Interceptor[] inters = new Interceptor[]{ debug};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);
		sql.setSQLIdNameConversion(new PackagePathIdNameConversion());
//		sql.unique(User.class, 1);
		UserDao dao = sql.getMapper(UserDao.class);
		
		PageQuery pageQuery = new PageQuery();
		
//		sql.pageQuery("org.beetl.sql.test.user.getIds3", User.class,pageQuery);
//		sql.pageQuery("user.getIds3", User.class,pageQuery);
		
//		unique(sql,1);
//		dao.unique(1);
		dao.getIds3(pageQuery);
		
			
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


