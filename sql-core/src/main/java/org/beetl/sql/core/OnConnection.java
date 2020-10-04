package org.beetl.sql.core;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * 直接获得Connecton，通常用于存储过程等beetlsql不支持的地方，需要注意，参数Connection 不要关闭，应该交给Beetlsql 协调框架，让
 * 框架负责关闭Connection
 * @author xiandafu
 * @param <T>
 */
public abstract class OnConnection<T> {
    protected SQLManager sqlManagaer = null;
	public abstract T call(Connection conn) throws SQLException ;
	/**
	 * 获得数据库连接，默认返回master
	 * @param cs
	 * @return
	 */
	public Connection getConn(ConnectionSource cs){
		return cs.getMasterConn();
	}
    public SQLManager getSqlManagaer() {
        return sqlManagaer;
    }
    public void setSqlManagaer(SQLManager sqlManagaer) {
        this.sqlManagaer = sqlManagaer;
    }
	
}
