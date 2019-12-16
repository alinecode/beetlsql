package org.beetl.sql.test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.exception.ErrorInfo;
import org.beetl.sql.core.*;
import org.beetl.sql.core.annotatoin.builder.SampleJsonAtrributeBuilder;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * pom.xml  中过滤了跟test相关的包，sql资源文件，想要运行起来，需要暂时去掉exclude
 * @author xiandafu
 *
 */

public class QuickTest {
	static ObjectMapper  mapper = new ObjectMapper();
	public List list = null;

	public static void main(String[] args) throws Exception {


		// DB2SqlStyle style = new DB2SqlStyle();
		// SqlServerStyle style = new SqlServerStyle();
		// SqlServer2012Style style = new SqlServer2012Style();
		// OracleStyle style = new OracleStyle();
		// PostgresStyle style = new PostgresStyle();
		MySqlStyle style = new MySqlStyle();
		ConnectionSource cs = ConnectionSourceHelper.getSingle(datasource());

		SQLLoader loader = new ClasspathLoader("/sql");
		DebugInterceptor debug = new DebugInterceptor();

		Interceptor[] inters = new Interceptor[]{debug};
		final SQLManager sql = new SQLManager(style, loader, cs, new UnderlinedNameConversion(), inters);
		//预先注册一个，否则没有办法使用@Jackson注解
		sql.getBeetl().getGroupTemplate().registerFunction("jackson", SampleJsonAtrributeBuilder.json);
		sql.addIdAutonGen("test", new IDAutoGen() {
			int i = 0;

			@Override
			public Object nextID(String params) {
				return i++;
			}
		});

		UserDao dao = sql.getMapper(UserDao.class);
		dao.createQuery().unique();
		dao.queryAll();




	}


//	static class MyPageQuery extends  PageQuery{
//
//	}

	static  class MyPageQuery<T> extends org.beetl.sql.core.engine.PageQuery {
		private static final long serialVersionUID = 6114194234926721407L;

		@Override
		public void setParas(Object paras) {
			super.setParas(paras);
		}

		@Override
		public void setOrderBy(String orderBy) {

			super.setOrderBy(orderBy);
		}
	}

	static public JavaType parameterizedType(Class c, Type pt) {
		if (pt instanceof ParameterizedType) {
			Type[] tv = ((ParameterizedType) pt).getActualTypeArguments();

			Class[] types = new Class[tv.length];
			for (int i = 0; i < tv.length; i++) {
				//如果还是范型
				types[i] = ((Class) tv[i]);
			}

			return getCollectionType(c, types);
		} else {
			throw new IllegalStateException(pt.toString());
		}


	}


	static public JavaType getCollectionType(Class collectionClass, Class[] elementClasses) {

		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	public static User unique(SQLManager sql, Object key) {
		return sql.unique(User.class, key);
	}

	public static DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(MysqlDBConfig.url);
		ds.setUsername(MysqlDBConfig.userName);
		ds.setPassword(MysqlDBConfig.password);
		ds.setDriverClassName(MysqlDBConfig.driver);
		// ds.setAutoCommit(false);
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
