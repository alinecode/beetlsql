package org.beetl.sql.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

public class DefaultConnectionSource implements ConnectionSource{
	protected DataSource master = null;
	protected DataSource[] slaves = null;
	protected ThreadLocal<Integer> forceStatus = new ThreadLocal<Integer>(){
		protected Integer initialValue() {
	        return 0;
	    }

	};
	
	
	
	public DefaultConnectionSource(){
		
	}
	public DefaultConnectionSource(DataSource master,DataSource[] slaves){
		this.master = master;
		this.slaves = slaves;
		
	}
	
	@Override
	public Connection getConn(String sqlId,boolean isUpdate,String sql,List<?> paras){
		if(this.slaves==null||this.slaves.length==0) return this.getWriteConn(sqlId,sql,paras);		
		if(isUpdate) return this.getWriteConn(sqlId,sql,paras);
		int status  = forceStatus.get();
		if(status ==0||status==1){
			return this.getReadConn(sqlId, sql, paras);
		}else{
			return this.getWriteConn(sqlId,sql,paras);
		}
		
		
	}
	
	@Override
	public Connection getMaster() {
		return this.doGetConnectoin(master);		
	}
	
	protected  Connection getReadConn(String sqlId,String sql,List<?> paras) {
		if(slaves==null||slaves.length==0) return getWriteConn(sqlId,sql,paras);
		else{
		
			return nextSlaveConn();
		}
	}
	
	protected Connection getWriteConn(String sqlId,String sql,List<?> paras) {
		
			return doGetConnectoin(master);
	
	}
	
	protected Connection nextSlaveConn(){
		//随机，todo，换成顺序
		DataSource ds = slaves[new Random().nextInt(slaves.length)];
		return doGetConnectoin(ds);
	}
	
	protected Connection doGetConnectoin(DataSource ds){
		try {
			if(DSTransactionManager.inTrans()){
				return DSTransactionManager.getCurrentThreadConnection(ds);
			}else{
				return ds.getConnection();
			}
			
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
	public boolean isTransaction() {
		return DSTransactionManager.inTrans();
	}
	@Override
	public Connection getSlave() {
		if(this.slaves!=null&&this.slaves.length!=0){
			return nextSlaveConn();
		}else{
			return this.getMaster();
		}
	}
	@Override
	public void forceBegin(boolean isMaster) {
		forceStatus.set(isMaster?2:1);
		
	}
	@Override
	public void forceEnd() {
		forceStatus.set(0);		
	}

	
	
	
}