package org.beetl.sql.ext.jfinal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.beetl.sql.core.DSTransactionManager;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class Trans implements Interceptor {

	public static void start() throws SQLException {
		DSTransactionManager.start();
	}
	public static void commit() throws SQLException {
		DSTransactionManager.commit();
	}
	
	public static void rollback() throws SQLException {
		DSTransactionManager.rollback();
	}
		@Override
	public void intercept(Invocation inv) {
		try{
			DSTransactionManager.start();
			inv.invoke();
			DSTransactionManager.commit();
		}catch(SQLException ex ){
			try {
				DSTransactionManager.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throw new RuntimeException(ex);
		}
		catch(RuntimeException ex){
			
			try {
				DSTransactionManager.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throw ex;
		}finally{
			DSTransactionManager.clear();
			
		}
		
	
	}

	static boolean inTrans(){
		return DSTransactionManager.inTrans();
	}
	
	
	static Connection getCurrentThreadConnection(DataSource ds) throws SQLException{
			return  DSTransactionManager.getCurrentThreadConnection(ds);
		
	}
}
