package org.beetl.sql.ext.spring;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.ExecuteContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author xiandafu
 */
public class SpringConnectionSource extends DefaultConnectionSource {

	public SpringConnectionSource() {
		super();
	}

	public SpringConnectionSource(DataSource master, DataSource[] slaves) {
		super(master, slaves);
	}

	@Override
	public Connection getConn(ExecuteContext ctx, boolean isUpdate){
		if (this.slaves == null || this.slaves.length == 0)
			return this.getWriteConn(ctx);
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
	protected Connection doGetConnection(DataSource ds) {
		try {
			Connection connection =  DataSourceUtils.getConnection(ds);
			return connection;
		} catch (CannotGetJdbcConnectionException ex) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION, ex);
		}

	}



	public DataSource getMasterSource() {
		return master;
	}

	public void setMasterSource(DataSource master) {
		this.master = master;
	}

	public DataSource[] getSlaveSource() {
		return slaves;
	}

	public void setSlaveSource(DataSource[] slaves) {
		this.slaves = slaves;
	}
}