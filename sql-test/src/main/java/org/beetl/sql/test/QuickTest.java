package org.beetl.sql.test;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.call.CallReady;
import org.beetl.sql.core.call.InArg;
import org.beetl.sql.core.call.OutArg;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 内部测试新功能或者bug用，所有单元测试参考test目录
 * @author xiandafu
 *
 */

public class QuickTest {

	static DataSource dataSource = datasource();
	private static   DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");
		return ds;
	}
	private  static SQLManager getSQLManager(){

		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		builder.setProduct(false);
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}

	public static void main(String[] args) throws Exception {
		SQLManager sqlManager = getSQLManager();

		OrderLogMapper orderLogMapper = sqlManager.getMapper(OrderLogMapper.class);
		Class c  = orderLogMapper.getClass();
		System.out.println(Arrays.asList(c.getMethods()));
		Method method = c.getMethod("all",new Class[]{});
		method.invoke(orderLogMapper,new Object[]{});


//		List<OrderLog> list = orderLogMapper.callSample(1,outHolder);
//		System.out.println(outHolder.getName());
////		System.out.println(ret);
//		System.out.println(list);




//		CallReady callReady = new CallReady("call test.selectStu(?,?)");
//		callReady.add(new InArg(1));
//		OutArg nameOut = new OutArg(String.class);
//		callReady.add(nameOut);
////		callReady.add(new OutArg(Integer.class)).add(new OutArg(String.class));
//		List<OrderLog> users = sqlManager.executeCall(callReady,OrderLog.class);
//		String name = (String)nameOut.getOutValue();
//
//		System.out.println(name);
////		System.out.println(ret);
//		System.out.println(users);




//		sqlManager.executeOnConnection(new OnConnection<Object>() {
//			@Override
//			public Object call(Connection conn) throws SQLException {
//				CallableStatement  call = conn.prepareCall("CALL test.mytest(?,?)");
//				call.registerOutParameter(1, Types.INTEGER);
//				call.registerOutParameter(2, Types.VARCHAR);
//				ResultSet resultSet = call.executeQuery();
//				int cout = call.getInt(1);
//				String ret = call.getString(2);
//				System.out.println(cout);
//				System.out.println(ret);
//				while (resultSet.next()){
//					System.out.println(resultSet.getObject(1));
//
//				}
//
//				return null;
//			}
//		});
	}


	public static DataSource mysqlDatasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(MysqlDBConfig.url);
		ds.setUsername(MysqlDBConfig.userName);
		ds.setPassword(MysqlDBConfig.password);
		ds.setDriverClassName(MysqlDBConfig.driver);
		ds.setLeakDetectionThreshold(10);
		ds.setMaximumPoolSize(1);
		// ds.setAutoCommit(false);
		return ds;
	}



	public static class MysqlDBConfig {
		//    public static String driver = "com.mysql.jdbc.Driver";
		public static String driver = "com.mysql.cj.jdbc.Driver";
		public static String dbName = "test";
		public static String password = "123456";
		public static String userName = "root";
		public static String url = "jdbc:mysql://127.0.0.1:3306/" + dbName + "?&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true";
	}

}
