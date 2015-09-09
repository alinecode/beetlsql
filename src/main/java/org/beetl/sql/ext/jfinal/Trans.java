package org.beetl.sql.ext.jfinal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class Trans implements Interceptor {

	static ThreadLocal<Boolean> inTrans = new ThreadLocal<Boolean> (){
		  protected Boolean initialValue() {
		        return false;
		    }
	};
	
	static ThreadLocal<Map<DataSource,Connection>> conns = new  ThreadLocal<Map<DataSource,Connection>>();
	@Override
	public void intercept(Invocation inv) {
		try{
			inTrans.set(true);
			inv.invoke();
			commit();
		}catch(RuntimeException ex){
			
			rollback();
			throw ex;
		}finally{
			inTrans.set(false);
			
		}
		
	
	}

	static boolean inTrans(){
		return inTrans.get();
	}
	
	static void commit(){
		Map<DataSource,Connection> map = conns.get();
		//
		if(map==null) return ; 
		for(Connection conn:map.values()){
			try{
				conn.commit();
				
			}catch(SQLException ex){
				System.err.println("commit error of connection "+conn);
				
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {
					System.err.println("commit error of connection "+conn);
				}
			}
			
		}
		conns.remove();
	}
	
	static void rollback(){
		Map<DataSource,Connection> map = conns.get();
		if(map==null) return ; 
		for(Connection conn:map.values()){
			try{
				conn.rollback();
				
			}catch(SQLException ex){
				ex.printStackTrace();
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		conns.remove();
	}
	static Connection getCurrentThreadConnection(DataSource ds) throws SQLException{
		Map<DataSource,Connection>  map = conns.get();
		Connection conn = null;
		if(map==null){
			map = new HashMap<DataSource,Connection>();
			conn = ds.getConnection();
			//如果用户还有不需要事物，且每次都提交的操作，这个需求很怪，不管了
			conn.setAutoCommit(false);
			map.put(ds, conn);
			conns.set(map);
		}else{
			conn = map.get(ds);
			if(conn!=null) return conn;
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			map.put(ds, conn);
			
		}
		return conn;
		
	}
}
