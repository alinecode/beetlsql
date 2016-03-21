
package org.beetl.sql.ext.spring4;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.ext.spring.SpringConnectionSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * BeetlSql数据源
 * @author woate
 */
public class BeetlSqlDataSource extends SpringConnectionSource {
   


}