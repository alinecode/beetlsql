package org.beetl.sql.ext.jfinal;

import com.jfinal.kit.PropKit;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;

import javax.sql.DataSource;
import java.util.Properties;

/**
 *
 * @author xiandafu
 */
public class JFinalBeetlSql {
	static SQLManager sqlManager = null;
	static String nc = null;
	static String sqlRoot = null;
	static String dbStyle = null;
	static String[] ins = null;
	static JFinalConnectonSource ds = null;

	public static void init() {
		HikariSource source = new HikariSource(PropKit.getProp().getProperties());
		source.start();
		ds = new JFinalConnectonSource(source.getDataSource(), null);
		initProp();
		initSQLMananger();

	}

	public static void init(DataSource master, DataSource[] slaves) {
		ds = new JFinalConnectonSource(master, slaves);
		initProp();
		initSQLMananger();

	}



	private static void initProp() {
		nc = PropKit.get("sql.nc", "org.beetl.sql.core.HumpNameConversion");
		sqlRoot = PropKit.get("sql.root", "/sql");
		String interceptors = PropKit.get("sql.interceptor");
		ins = null;
		if (interceptors != null) {
			ins = interceptors.split(",");
		}
		dbStyle = PropKit.get("sql.dbStyle", "org.beetl.sql.core.db.MySqlStyle");
	}

	private static void initSQLMananger() {

		DBStyle dbStyleIns = (DBStyle) instance(dbStyle);
		MarkdownClasspathLoader sqlLoader = new MarkdownClasspathLoader(sqlRoot);
		NameConversion ncIns = (NameConversion) instance(nc);
		Interceptor[] inters = null;
		if (ins != null) {
			inters = new Interceptor[ins.length];
			//add suxj 2015/08/25
			for (int i = 0; i < inters.length; i++) {
				inters[i] = (Interceptor) instance(ins[i]);
			}
		} else {
			inters = new Interceptor[0];
		}


		SQLManagerBuilder builder = new SQLManagerBuilder(ds);
		builder.setNc(ncIns);
		builder.setInters(inters);
		builder.setDbStyle(dbStyleIns);
		builder.setSqlLoader(sqlLoader);
		sqlManager = builder.build();

	}

	private static Object instance(String clsName) {
		Object c;
		try {
			c = Class.forName(clsName).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("初始化类错误" + clsName, e);
		}
		return c;
	}


	public static SQLManager dao() {
		if (sqlManager != null)
			return sqlManager;
		else {
			throw new RuntimeException("未初始化，需要调用init方法");
		}
	}


}

class HikariSource {
	private String jdbcUrl;
	private String user;
	private String password;
	private String driverClass = "com.mysql.jdbc.Driver";
	private int maxPoolSize = 100;
	private int minPoolSize = 10;
	private int initialPoolSize = 10;
	private int maxIdleTime = 20;
	private int acquireIncrement = 2;

	private HikariDataSource dataSource;

	public HikariSource(Properties properties) {

		Properties ps = properties;
		initC3p0Properties(ps.getProperty("jdbcUrl")
				, ps.getProperty("user")
				, ps.getProperty("password"),
				ps.getProperty("driverClass")
				, toInt(ps.getProperty("maxPoolSize", "5"))
				,toInt(ps.getProperty("minPoolSize", "5"))
				, toInt(ps.getProperty("initialPoolSize", "5"))
				);
	}

	private void initC3p0Properties(String jdbcUrl, String user, String password, String driverClass,
			Integer maxPoolSize, Integer minPoolSize, Integer initialPoolSize) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.driverClass = driverClass != null ? driverClass : this.driverClass;
		this.maxPoolSize = maxPoolSize != null ? maxPoolSize : this.maxPoolSize;
		this.minPoolSize = minPoolSize != null ? minPoolSize : this.minPoolSize;
		this.initialPoolSize = initialPoolSize != null ? initialPoolSize : this.initialPoolSize;

	}

	public boolean start() {

		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(jdbcUrl);
		ds.setUsername(user);
		ds.setPassword(password);
		ds.setDriverClassName(driverClass);
		ds.setMaximumPoolSize(maxPoolSize);
		ds.setMinimumIdle(minPoolSize);


		return true;
	}

	private Integer toInt(String str) {
		return Integer.parseInt(str);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public boolean stop() {
		if (dataSource != null)
			dataSource.close();
		return true;
	}
}
