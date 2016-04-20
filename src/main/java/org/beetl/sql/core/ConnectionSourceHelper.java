package org.beetl.sql.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

public class ConnectionSourceHelper {
	public  static ConnectionSource getSingle(DataSource ds){
		return new DefaultConnectionSource(ds,null);
	}
	public  static ConnectionSource getMasterSlave(DataSource ds,DataSource[] slaves){
		return new DefaultConnectionSource(ds,slaves);
	}
	/**
	 *  多余参数 dbName
	 * @param driver
	 * @param url
	 * @param dbName 多余了
	 * @param userName
	 * @param password
	 * @return
	 */
	@Deprecated
	public static ConnectionSource getSimple(String driver,String url,String dbName,String userName,String password){
		return new SimpleConnectoinSource(driver,url,userName,password);
	}
	
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
	public Connection getMaster() {
		return _getConn();
	}

	@Override
	public Connection getConn(String sqlId, boolean isUpdate, String sql, List paras) {
		return _getConn();
	}

	
	@Override
	public boolean isTransaction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getSlave() {
		return this.getMaster();
	}
	@Override
	public void forceBegin(boolean isMaster) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void forceEnd() {
		// TODO Auto-generated method stub
		
	}
	
}
