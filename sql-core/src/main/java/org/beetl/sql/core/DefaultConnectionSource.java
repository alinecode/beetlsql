package org.beetl.sql.core;

import org.beetl.sql.clazz.kit.BeetlSQLException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

/**
 * 一个默认的，框架只需要提供{@code DataSource}既可以集成到beetlsql里
 * @author xiandafu
 */
public class DefaultConnectionSource implements ConnectionSource {
	protected DataSource master = null;
	protected DataSource[] slaves = null;

	public DefaultConnectionSource() {
	}

	public DefaultConnectionSource(DataSource master, DataSource[] slaves) {
		this.master = master;
		this.slaves = slaves;

	}

	@Override
	public Connection getConn(ExecuteContext ctx, boolean isUpdate) {
		if(getForceDataSource()!=null){
			try {
				return getForceDataSource().getConnection();
			} catch (SQLException e) {
				throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION, e);
			}
		}

		if (this.slaves == null || this.slaves.length == 0) {
			return this.getWriteConn(ctx);
		}
		if (isUpdate) {
			return this.getWriteConn(ctx);
		} else {
			return this.getReadConn(ctx);
		}

	}

	@Override
	public Connection getMasterConn() {
		return doGetConnection(null,master);

	}

	protected Connection getReadConn(ExecuteContext ctx) {
		if (slaves == null || slaves.length == 0) {
			return getWriteConn(ctx);
		} else {

			return nextSlaveConn(ctx);
		}
	}

	protected Connection getWriteConn(ExecuteContext ctx) {

		return doGetConnection(ctx, master);

	}

	protected Connection nextSlaveConn(ExecuteContext ctx) {
		//随机，todo，换成顺序
		DataSource ds = slaves[new Random().nextInt(slaves.length)];
		return doGetConnection(ctx, ds);
	}

	protected Connection doGetConnection(ExecuteContext ctx, DataSource ds) {
		try {
			if (DSTransactionManager.inTrans()) {
				return DSTransactionManager.getCurrentThreadConnection(ds);
			} else {
				return ds.getConnection();
			}

		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION, e);
		}
	}
	@Override
	public DataSource getMasterSource() {
		return master;
	}

	public void setMasterSource(DataSource master) {
		this.master = master;
	}

	@Override
	public boolean isTransaction() {
		return DSTransactionManager.inTrans();
	}

	@Override
	public Connection getMetaData() {
		return this.getMasterConn();
	}

	@Override
	public DataSource[] getSlaves() {
		return slaves;
	}

}