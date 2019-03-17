package org.beetl.sql.core;

import java.util.Collections;
import java.util.List;

/**
 * JDBC  批量更新或者插入接口
 * @author xiandafu
 *
 */
public class SQLBatchReady {
	 String sql = null;
	 List<Object[] > args = null;
	 public SQLBatchReady(String sql) {
	        this(sql, null);
	  }
	 public SQLBatchReady(String sql, List<Object[]> args) {
        this.sql = sql;
        if (args == null) {
            this.args = Collections.emptyList();
        } else {
            this.args = args;
        }

	 }
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<Object[]> getArgs() {
		return args;
	}
	public void setArgs(List<Object[]> args) {
		this.args = args;
	}
	 
	 

}
