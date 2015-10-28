package org.beetl.sql.core;

public class SQLReady {
	Object[] args;
	String sql = null;
	public SQLReady(String sql,Object... args){
		this.sql = sql ;
		this.args = args;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	
}
