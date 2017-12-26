package org.beetl.sql.test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.poi.hssf.record.formula.functions.T;
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
		
		
//		DB2SqlStyle style = new DB2SqlStyle();
		MySqlStyle style = new MySqlStyle();
//		OracleStyle style = new OracleStyle();
//		
		
		ConnectionSource cs  = ConnectionSourceHelper.getSingle(datasource());
		
		SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
		DebugInterceptor debug = new DebugInterceptor(QuickTest.class.getName());
	
				
		
		Interceptor[] inters = new Interceptor[]{ debug};
		SQLManager 	sql = new SQLManager(style,loader,cs,new UnderlinedNameConversion(), inters);
		
		BloodRelationship1 s = sql.unique(BloodRelationship1.class, 1);
		
		BloodRelationshipVO vo = new BloodRelationshipVO();
		vo.setId(s.getId());
		vo.setUserId(s.getUserId());
		vo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		BloodRelationshipVO vo1 = new BloodRelationshipVO();
		vo1.setId(2);
		vo1.setUserId(s.getUserId());
		
	
		List list = Arrays.asList(vo1,vo);
		sql.updateBatchTemplateById(BloodRelationship1.class, list);
//		sql.updateTemplateById(vo);
		
//		UserDao dao = sql.getMapper(UserDao.class);
//		
//		PageQuery query = new PageQuery();
//		query.setPara("a", "b");
//		dao.getIds3(query);
//		
////		List<User> list = dao.createQuery().andEq("name", "hi").single();
//		User user  = dao.createQuery().andEq("name", "hi").unique();
	
//		//jdk 8 允许写法
//		List<User> list1  = sql.query(User.class).lamdba().andEq(User::getName, "hi").orderBy(User::getId).select();
		
			
	}
	
	static void test(Function<T,?> fun) {
		System.out.println(fun);
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


