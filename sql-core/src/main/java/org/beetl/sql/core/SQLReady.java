package org.beetl.sql.core;

import lombok.Data;

/**
 * 封装了jdbc 和参数。
 *
 * @author xiandafu
 */
@Data
public class SQLReady {
    Object[] args;
    String sql = null;
    SqlId sqlId = null;

    public SQLReady(String sql) {
        this(sql, (Object[])null);
    }

    /**
     * @param sql  带”？“的 sql语句
     * @param args 参数
     */
    public SQLReady(String sql, Object... args) {
        this.sql = sql;
        if (args == null) {
            this.args = new Object[0];
        } else {
            this.args = args;
        }

    }

	public SQLReady(SqlId sqlId,String sql, Object... args) {
    	this(sql,args);
    	this.sqlId = sqlId;

	}


}
