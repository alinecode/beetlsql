package org.beetl.sql.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.OnConnection;
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
		
		
//		DB2SqlStyle style = new DB2SqlStyle();
//		SqlServerStyle style = new SqlServerStyle();
//		SqlServer2012Style style = new SqlServer2012Style();
//		OracleStyle style = new OracleStyle();
		MySqlStyle style = new MySqlStyle();
//		PostgresStyle style = new PostgresStyle();
		
		ConnectionSource cs  = ConnectionSourceHelper.getSingle(datasource());
		
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		DebugInterceptor debug = new DebugInterceptor(QuickTest.class.getName());
		
				
		
		Interceptor[] inters = new Interceptor[]{ debug};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);
      User template = new User();
//          template.setName("abc");
//		sql.template(User.class, template, " id desc");
//		UserDao dao = sql.getMapper(UserDao.class);
//		User t = new User();
//		t.setrType("a");
//		t.setId(99);
//		dao.insertTemplate(t);
      
      List<User> list = sql.executeOnConnection(new OnConnection<List<User>>() {

        @Override
        public List<User> call(Connection conn) throws SQLException {
            String call = "{call call_user()}";
            CallableStatement callableStatement = conn.prepareCall(call); 
            ResultSet rs = callableStatement.executeQuery();
            return this.sqlManagaer.getDefaultBeanProcessors().toBeanList(rs,User.class);
        }
          
      });
      
      System.out.print(list.size());
      
		
//		dao.select(new HashMap());
//		sql.lambdaQuery(User.class).andEq("id", 1).updateSelective(template);
//		sql.template(template);
//		dao.deleteById(199);

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
//		ds.setAutoCommit(false);
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



