package org.beetl.sql.ext.jfinal;


import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.ExecuteContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JFinalConnectonSource extends DefaultConnectionSource {



	public JFinalConnectonSource(DataSource master, DataSource[] slaves) {
		super(master, slaves);

	}

	@Override
    protected Connection doGetConnection(ExecuteContext ctx,DataSource ds) {
		try {
			if (Trans.inTrans()) {
				return Trans.getCurrentThreadConnection(ds);
			} else {
				//非事物环境,用户自己管理了
				return ds.getConnection();
			}

		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION, e);
		}
	}

	@Override
    public boolean isTransaction() {
		return Trans.inTrans();
	}

}
