package org.beetl.sql.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

public class DefaultConnectionSource implements ConnectionSource{
	protected DataSource master = null;
	protected DataSource[] slaves = null;
	protected ThreadLocal<Boolean> localMaster = new ThreadLocal<Boolean>(){
		protected Boolean initialValue() {
	        return false;
	    }

	};
	
	
	protected ThreadLocal<Boolean> localSlave = new ThreadLocal<Boolean>(){
		protected Boolean initialValue() {
	        return false;
	    }

	};
	
	public DefaultConnectionSource(){
		
	}
	public DefaultConnectionSource(DataSource master,DataSource[] slaves){
		this.master = master;
		this.slaves = slaves;
		
	}
	
	@Override
	public Connection getConn(String sqlId,boolean isUpdate,String sql,List paras){
		if(this.slaves==null||this.slaves.length==0) return this.getWriteConn(sqlId,sql,paras);		
		if(isUpdate) return this.getWriteConn(sqlId,sql,paras);
		boolean onlyMaster = localMaster.get();
		if(onlyMaster) return this.getMaster();	
		return this.getReadConn(sqlId, sql, paras);
	}
	
	@Override
	public Connection getMaster() {
		return this.doGetConnectoin(master);		
	}
	
	protected  Connection getReadConn(String sqlId,String sql,List paras) {
		if(slaves==null||slaves.length==0) return getWriteConn(sqlId,sql,paras);
		else{
		
			return nextSlaveConn();
		}
	}
	
	protected Connection getWriteConn(String sqlId,String sql,List paras) {
		
			return doGetConnectoin(master);
	
	}
	
	protected Connection nextSlaveConn(){
		//随机，todo，换成顺序
		DataSource ds = slaves[new Random().nextInt(slaves.length)];
		return doGetConnectoin(ds);
	}
	
	protected Connection doGetConnectoin(DataSource ds){
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION,e);
		}
	}
	public DataSource getMasterSource() {
		return master;
	}
	public void setMasterSource(DataSource master) {
		this.master = master;
	}
	public DataSource[] getSlaves() {
		return slaves;
	}
	public void setSlaves(DataSource[] slaves) {
		this.slaves = slaves;
	}
	@Override
	public void onlyMasterBegin() {
		localMaster.set(true);
		
	}
	@Override
	public void onlyMasterEnd() {
		localMaster.set(false);
		
	}
	@Override
	public boolean isTransaction() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Connection getSlave() {
		if(this.slaves!=null&&this.slaves.length!=0){
			return nextSlaveConn();
		}else{
			return this.getMaster();
		}
	}

	
	
	
}