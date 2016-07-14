package org.beetl.sql.core;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * 直接获得Connecton，通常用于存储过程等beetlsql不支持的地方
 * @author xiandafu
 *
 * @param <T>
 */
public abstract class OnConnection<T> {
	public abstract T call(Connection conn) throws SQLException ;
	/**
	 * 获得数据库连接，默认返回master
	 * @param cs
	 * @return
	 */
	public Connection getConn(ConnectionSource cs){
		return cs.getMaster();
	}
}
