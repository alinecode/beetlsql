package org.beetl.sql.jooq;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.ErrorDebugInterceptor;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GenerationTool;
import org.jooq.impl.DSL;
import org.jooq.meta.Definition;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.jaxb.Jdbc;

import javax.sql.DataSource;

import static org.jooq.codegen.GeneratorStrategy.Mode.DEFAULT;


public class Main {
	public static void main(String[] args) throws Exception {
		{
			SQLManager sqlManager = getSQLManager();
			DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");

			JooqCodeGen codeGen = new JooqCodeGen("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE","sa",""
				,"org.h2.Driver","");

			codeGen.genCode(null,"public",false);

//			JooqHelper jooqHelper = new JooqHelper(sqlManager);
//			{
//				List<ReLog> lists = jooqHelper.executeQuery(ReLog.class,
//					jooqHelper.ctx()
//						.select().from(ORDER_LOG).where(ORDER_LOG.VERSION.eq(100))
//					);
//				System.out.println(lists);
//			}
//			{
//				DSLContext create = DSL.using(con, SQLDialect.H2);
//				String sql = create.select().from(ORDER_LOG).getSQL();
//				System.out.println(sql);
//				Result<org.jooq.Record> result = create.select().from(ORDER_LOG).fetch();
//				for (org.jooq.Record r : result) {
//					Integer id = r.getValue(ORDER_LOG.ORDER_ID);
//					Integer version = r.getValue(ORDER_LOG.VERSION);
//					ReLog log = r.into(ReLog.class);
//
//					System.out.println("ID: " + id + " version: " + version);
//					System.out.println("ID: " + log.getOrderId() + " version: " + log.getVersion());
//
//				}
//			}
		}
	}

	public static class ReLog {
		Integer orderId;
		Integer version;

		public Integer getOrderId() {
			return orderId;
		}

		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}

		public Integer getVersion() {
			return version;
		}

		public void setVersion(Integer version) {
			this.version = version;
		}

		@Override
		public String toString() {
			return "ReLog{" +
				"orderId=" + orderId +
				", version=" + version +
				'}';
		}
	}
	public static class MyGeneratorStrategy  extends DefaultGeneratorStrategy {
		public String getJavaClassName(Definition definition, Mode mode){
			String name =  super.getJavaClassName(definition,mode);
			if(mode==DEFAULT){
				return name =name+"Query";
			}else{
				return name;
			}
		}
	}
	static DataSource dataSource = datasource();
	private static   DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");
		ds.setSchema(null);
		return ds;
	}
	private  static SQLManager getSQLManager(){

		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new ErrorDebugInterceptor()});
		builder.setDbStyle(new H2Style(){
			public int getMaxBatchCount() {
				return 10;
			}
		});
		builder.setProduct(false);
		SQLManager sqlManager = builder.build();
		return sqlManager;
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
		public static String password = "12345678";
		public static String userName = "root";
		public static String url = "jdbc:mysql://127.0.0.1:3306/" + dbName + "?&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true";
	}
}
