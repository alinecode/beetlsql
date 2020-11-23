package org.beetl.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.BeforeClass;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 单元测试基础类，用于初始化database 和 SQLManager
 *
 * https://www.testcontainers.org/
 */
public class BaseTest   {

	public static String testSqlFile ="/db/db-init.sql";
	public  static SQLManager sqlManager;
	public  static HikariDataSource dataSource ;


	public BaseTest(){

	}
	@BeforeClass
	public  static void start(){
		datasource();
		initSQLManager();
	}



	public  static   void datasource() {
		dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setDriverClassName("org.h2.Driver");

	}

	public static void initSQLManager(){
		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		sqlManager = builder.build();
	}

	public static void initTable(String file){
		initData(dataSource,file);
	}

	public static void initData(DataSource ds,String file)  {
		Connection conn = null;
		try{
			conn = ds.getConnection();
			String[] sqls = getSqlFromFile(file);
			for(String sql:sqls ){
				runSql(conn,sql);
			}

		}catch (SQLException sqlException){
			throw new RuntimeException(sqlException);
		}
		finally {
			try {
				if(conn!=null)conn.close();
			} catch (SQLException sqlException) {
				//ignore
			}
		}
	}

	private static String[] getSqlFromFile(String file){
		try{
			InputStream ins = BaseTest.class.getResourceAsStream(file);
			if(ins==null){
				throw new IllegalArgumentException("无法加载文件 "+file);
			}
			int len = ins.available();
			byte[] bs = new byte[len];
			ins.read(bs);
			String str = new String(bs,"UTF-8");
			String[] sql = str.split(";");
			return sql;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}

	}

	private static void runSql(Connection conn,String sql) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		ps.close();
	}


}
