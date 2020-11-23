package org.beetl.sql.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 一个简单的用来生成ConnectionSource的类
 */
public class ConnectionSourceHelper {

	public  static ConnectionSource getSingle(DataSource ds){
		return new DefaultConnectionSource(ds,null);
	}
	public  static ConnectionSource getMasterSlave(DataSource ds,DataSource[] slaves){
		return new DefaultConnectionSource(ds,slaves);
	}

	/**
	 * 通常用于验证，测试，demo
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @return
	 */
	public static ConnectionSource getSimple(String driver,String url,String userName,String password){
		return new SimpleConnectoinSource(driver,url,userName,password);
	}
	
}

class SimpleConnectoinSource implements ConnectionSource {
	
	String driver =null;
    String password = null;
    String userName = null;
    String url = null;
	public SimpleConnectoinSource(String driver,String url,String userName,String password){
		this.driver = driver;
		this.url = url;
		this.userName = userName;
		this.password = password;
	}
	private Connection _getConn(){
		
        Connection conn = null;
        try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName,
	                password);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("驱动未发现:"+driver,e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return conn;
	}


	@Override
    public Connection getMasterConn() {
		return _getConn();
	}


	@Override
    public Connection getConn(ExecuteContext ctx, boolean isUpdate) {
		return _getConn();
	}

	

	@Override
    public boolean isTransaction() {
		// TODO Auto-generated method stub
		return false;
	}


	public Connection getSlave() {
		return this.getMasterConn();
	}


	@Override
    public Connection getMetaData() {
		return this.getMasterConn();
	}
	
}
