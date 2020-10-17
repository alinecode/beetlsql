package org.beetl.sql.ext.jfinal;

import com.jfinal.kit.PropKit;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.BeetlSQLTemplateEngine;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
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

	public static void init( Properties properties) {
		HikariSource source = new HikariSource(properties);
		source.start();
		ds = new JFinalConnectonSource(source.getDataSource(), null);
		initProp();
		initSQLManager();


	}

	public static void init(DataSource master, DataSource[] slaves) {
		ds = new JFinalConnectonSource(master, slaves);
		initProp();
		initSQLManager();

	}



	private static void initProp() {
		nc = PropKit.get("sql.nc", org.beetl.sql.core.DefaultNameConversion.class.getName());
		sqlRoot = PropKit.get("sql.root", "sql");
		String interceptors = PropKit.get("sql.interceptor");
		ins = null;
		if (interceptors != null) {
			ins = interceptors.split(",");
		}
		dbStyle = PropKit.get("sql.dbStyle", MySqlStyle.class.getName());
	}

	private static void initSQLManager() {

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
		return sqlManager;
	}


}

class HikariSource {
	private String jdbcUrl;
	private String user;
	private String password;
	private String driverClass = "com.mysql.jdbc.Driver";
	private int maxPoolSize = 5;
	private int minPoolSize = 5;
	private int initialPoolSize = 5;


	private HikariDataSource dataSource;

	public HikariSource(Properties properties) {

		Properties ps = properties;
		configProperties(ps.getProperty("jdbcUrl")
				, ps.getProperty("user")
				, ps.getProperty("password"),
				ps.getProperty("driverClass")
				, toInt(ps.getProperty("maxPoolSize", "5"))
				,toInt(ps.getProperty("minPoolSize", "5"))
				, toInt(ps.getProperty("initialPoolSize", "5"))
				);
	}

	private void configProperties(String jdbcUrl, String user, String password, String driverClass,
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
		this.dataSource =ds;
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
