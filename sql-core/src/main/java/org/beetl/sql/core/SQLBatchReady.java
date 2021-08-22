package org.beetl.sql.core;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * JDBC  批量更新或者插入接口
 * @author xiandafu
 */
@Data
public class SQLBatchReady {
	 String sql = null;
	 List<Object[] > args = null;
	 SqlId sqlId = null;
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

	public SQLBatchReady(SqlId sqlId,String sql, List<Object[]> args){
	 	this(sql,args);
	 	this.sqlId = sqlId;
	}




}
