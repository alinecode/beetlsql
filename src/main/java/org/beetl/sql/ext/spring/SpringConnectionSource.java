package org.beetl.sql.ext.spring;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.DefaultConnectionSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 这个类将过时
 * @see SpringConnectionSource
 */
public class SpringConnectionSource extends  DefaultConnectionSource{
	
	
	@Override
	public Connection getConn(String sqlId,boolean isUpdate,String sql,List paras){
		//只有一个数据源
		if(this.slaves==null||this.slaves.length==0) return this.getWriteConn(sqlId,sql,paras);
		//如果是更新语句，也得走master
		if(isUpdate) return this.getWriteConn(sqlId,sql,paras);
		//如果api强制使用
		int status  = forceStatus.get();
		if(status==1){
			return this.getReadConn(sqlId, sql, paras);
		}else if(status ==2){
			return this.getWriteConn(sqlId,sql,paras);
		}
		
		//在事物里都用master，除了readonly事物
		boolean inTrans = TransactionSynchronizationManager.isActualTransactionActive();
		if(inTrans){
			boolean  isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
			if(!isReadOnly){
				return this.getWriteConn(sqlId,sql,paras);
			}
		}
		
		 return this.getReadConn(sqlId, sql, paras);
	}
	
	
	@Override
	public boolean isTransaction() {
		return TransactionSynchronizationManager.isActualTransactionActive();
	}
	
	protected Connection doGetConnectoin(DataSource ds) {
		try{
			return DataSourceUtils.getConnection(ds);
		}catch(CannotGetJdbcConnectionException ex){
	
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION,ex);
		}
		
	}
	

	
}