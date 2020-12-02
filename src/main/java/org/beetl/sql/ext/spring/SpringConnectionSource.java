package org.beetl.sql.ext.spring;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.DefaultConnectionSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * 这个类将过时
 * @see SpringConnectionSource
 */
public class SpringConnectionSource extends DefaultConnectionSource {
	/**
	 * 记录connection和datasouce关系，spring事务属性跟datasource有关，参考TransactionSynchronizationManager
	 */
	Map<Connection,DataSource> connMap = Collections.synchronizedMap(new LinkedHashMap(){
		protected boolean removeEldestEntry(Map.Entry eldest) {
			//防止内存溢出
			return this.size()>10000;
		}
	}) ;

	@Override
	public Connection getConn(String sqlId, boolean isUpdate, String sql, List paras) {
		//只有一个数据源
		if (this.slaves == null || this.slaves.length == 0)
			return this.getWriteConn(sqlId, sql, paras);
		//如果是更新语句，也得走master
		if (isUpdate)
			return this.getWriteConn(sqlId, sql, paras);
		//如果api强制使用
		int status = forceStatus.get();
		if (status == 1) {
			return this.getReadConn(sqlId, sql, paras);
		} else if (status == 2) {
			return this.getWriteConn(sqlId, sql, paras);
		}

		//在事物里都用master，除了readonly事物
		boolean inTrans = TransactionSynchronizationManager.isActualTransactionActive();
		if (inTrans) {
			boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
			if (!isReadOnly) {
				return this.getWriteConn(sqlId, sql, paras);
			}
		}

		return this.getReadConn(sqlId, sql, paras);
	}


	@Override
	public boolean isTransaction() {
		return TransactionSynchronizationManager.isActualTransactionActive();
	}

	protected Connection doGetConnectoin(DataSource ds) {
		try {
			Connection conn =  DataSourceUtils.getConnection(ds);
			connMap.put(conn,ds);
			return conn;
		} catch (CannotGetJdbcConnectionException ex) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION, ex);
		}

	}

	@Override
	public void applyStatementSetting(Connection conn, Statement statement) throws SQLException {
		DataSource dataSource = connMap.get(conn);
		if(dataSource==null){
			return ;
		}
		ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
		if(holder==null){
			return ;
		}
		if ( holder.hasTimeout()) {
			statement.setQueryTimeout(holder.getTimeToLiveInSeconds());
		}


	}

	@Override
	public void applyConnectionSetting(Connection conn) throws SQLException {
		//暂时不支持
		return ;
	}


}