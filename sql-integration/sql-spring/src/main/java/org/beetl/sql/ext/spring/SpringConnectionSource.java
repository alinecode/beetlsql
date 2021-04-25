package org.beetl.sql.ext.spring;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.ExecuteContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

/**
 * @author xiandafu
 */
public class SpringConnectionSource extends DefaultConnectionSource {
	private static final String DS_CONTEXT_PARAM ="_datasource";

	public SpringConnectionSource() {
		super();
	}

	public SpringConnectionSource(DataSource master, DataSource[] slaves) {
		super(master, slaves);

	}

	@Override
	public Connection getConn(ExecuteContext ctx, boolean isUpdate){
		DataSource ds = null;
		if (this.slaves == null || this.slaves.length == 0) {
			return this.getWriteConn(ctx);
		}
		//如果是更新语句，也得走master
		if (isUpdate){
			return this.getWriteConn(ctx);
		}

		//在事物里都用master，除了readonly事物
		boolean inTrans = TransactionSynchronizationManager.isActualTransactionActive();
		if (inTrans) {
			boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
			if (!isReadOnly) {
				return this.getWriteConn(ctx);
			}
		}



		return this.getReadConn(ctx);

	}



	@Override
	public boolean isTransaction() {
		return TransactionSynchronizationManager.isActualTransactionActive();
	}

	@Override
	protected Connection doGetConnection(ExecuteContext ctx,DataSource ds) {
		try {
			Connection connection =  DataSourceUtils.getConnection(ds);
			if(ctx!=null){
				ctx.setContextPara(DS_CONTEXT_PARAM,ds);;
			}
			return connection;
		} catch (CannotGetJdbcConnectionException ex) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION, ex);
		}

	}



	@Override
    public DataSource getMasterSource() {
		return master;
	}

	@Override
    public void setMasterSource(DataSource master) {
		this.master = master;
	}

	public DataSource[] getSlaveSource() {
		return slaves;
	}

	public void setSlaveSource(DataSource[] slaves) {
		this.slaves = slaves;
	}


	@Override
	public void applyStatementSetting(ExecuteContext ctx,Connection conn,Statement statement) throws SQLException {
		DataSource dataSource = getDatasourceFromContext(ctx);
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
	public void applyConnectionSetting(ExecuteContext ctx,Connection conn){
		//do nothing
	}

	@Override
	public void closeConnection(Connection conn,ExecuteContext ctx,boolean isUpdate){
		DataSource dataSource = getDatasourceFromContext(ctx);
		DataSourceUtils.releaseConnection(conn,dataSource);
	}

	protected  DataSource getDatasourceFromContext(ExecuteContext ctx){
		DataSource dataSource = (DataSource)ctx.getContextPara(DS_CONTEXT_PARAM);
		return dataSource;
	}


}