package org.beetl.sql.ext.jfinal;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.beetl.sql.core.DSTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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

	static boolean inTrans() {
		return DSTransactionManager.inTrans();
	}

	static Connection getCurrentThreadConnection(DataSource ds) throws SQLException {
		return DSTransactionManager.getCurrentThreadConnection(ds);

	}

	@Override
	public void intercept(Invocation inv) {
		try {
			DSTransactionManager.start();
			inv.invoke();
			DSTransactionManager.commit();
		} catch (SQLException ex) {
			try {
				DSTransactionManager.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throw new RuntimeException(ex);
		} catch (RuntimeException ex) {

			try {
				DSTransactionManager.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throw ex;
		} finally {
			DSTransactionManager.clear();

		}


	}
}
