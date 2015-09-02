package org.beetl.sql.ext.jfinal;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.DBStyle;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JFinalBeetlSql {
	static SQLManager sqlManager = null;
	static String nc = null;
	static String sqlRoot = null;
	static String dbStyle = null;
	static String[] ins = null;
	static JFinalConnectonSource ds = null;
	public static void init(){
		
		C3p0Source source = new C3p0Source(PropKit.getProp().getProperties());
		source.start();
		 ds = new JFinalConnectonSource(source.getDataSource(),null);
		 initProp();
		 initSQLMananger();
		 
	}
	
	
	public static void init(DataSource master,DataSource[] slaves){
		 ds = new JFinalConnectonSource(master,slaves);
		 initProp();
		 initSQLMananger();
		 
	}
	
	
	
	private static void initProp(){
		 nc = PropKit.get("sql.nc","org.beetl.sql.core.HumpNameConversion");
		 sqlRoot =  PropKit.get("sql.root","/sql");
		String interceptors = PropKit.get("sql.interceptor");
		ins = null;
		if(interceptors!=null){
			ins = interceptors.split(",");
		}
		dbStyle = PropKit.get("sql.dbStyle","org.beetl.sql.core.db.MySqlStyle");
	}
	
	private static void initSQLMananger(){
		
		DBStyle dbStyleIns = (DBStyle)instance(dbStyle);
		SQLLoader sqlLoader = new ClasspathLoader(sqlRoot);
		NameConversion ncIns = (NameConversion)instance(nc);
		Interceptor[] inters = null;
		if(ins!=null){
			inters = new Interceptor[ins.length];
			//add suxj 2015/08/25
			for(int i=0 ;i<inters.length ;i++){
				inters[i] = (Interceptor) instance(ins[i]);
			}
		}else{
			inters = new Interceptor[0];
		}
		sqlManager = new SQLManager(dbStyleIns,sqlLoader,ds,ncIns,inters);
	}
	
	private static Object  instance(String clsName){
		Object c;
		try {
			c = Class.forName(clsName).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("初始化类错误"+clsName,e);
		} 
		return c ;
	}
	

	
	public static SQLManager dao(){
		if(sqlManager!=null)
		return sqlManager;
		else{
			throw new RuntimeException("未初始化，需要调用init方法");
		}
	}
	
	
}

class C3p0Source{
	private String jdbcUrl;
	private String user;
	private String password;
	private String driverClass = "com.mysql.jdbc.Driver";
	private int maxPoolSize = 100;
	private int minPoolSize = 10;
	private int initialPoolSize = 10;
	private int maxIdleTime = 20;
	private int acquireIncrement = 2;
	
	private ComboPooledDataSource dataSource;

	public C3p0Source(Properties properties) {
		
		Properties ps = properties;
		initC3p0Properties(ps.getProperty("jdbcUrl"), ps.getProperty("user"), ps.getProperty("password"), ps.getProperty("driverClass"),
				toInt(ps.getProperty("maxPoolSize","5")), toInt(ps.getProperty("minPoolSize","5")), toInt(ps.getProperty("initialPoolSize","5")),
				toInt(ps.getProperty("maxIdleTime","20")),toInt(ps.getProperty("acquireIncrement","5")));
	}
	private void initC3p0Properties(String jdbcUrl, String user, String password, String driverClass, Integer maxPoolSize, Integer minPoolSize, Integer initialPoolSize, Integer maxIdleTime, Integer acquireIncrement) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.driverClass = driverClass != null ? driverClass : this.driverClass;
		this.maxPoolSize = maxPoolSize != null ? maxPoolSize : this.maxPoolSize;
		this.minPoolSize = minPoolSize != null ? minPoolSize : this.minPoolSize;
		this.initialPoolSize = initialPoolSize != null ? initialPoolSize : this.initialPoolSize;
		this.maxIdleTime = maxIdleTime != null ? maxIdleTime : this.maxIdleTime;
		this.acquireIncrement = acquireIncrement != null ? acquireIncrement : this.acquireIncrement;
	}
	public boolean start() {
		dataSource = new ComboPooledDataSource();
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		try {dataSource.setDriverClass(driverClass);}
		catch (PropertyVetoException e) {dataSource = null; System.err.println("C3p0Plugin start error"); throw new RuntimeException(e);} 
		dataSource.setMaxPoolSize(maxPoolSize);
		dataSource.setMinPoolSize(minPoolSize);
		dataSource.setInitialPoolSize(initialPoolSize);
		dataSource.setMaxIdleTime(maxIdleTime);
		dataSource.setAcquireIncrement(acquireIncrement);
		
		
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
