package org.beetl.sql.core;
/**
 * 
 * 封装了jdbc 和参数。
 * @author xiandafu
 *
 */
public class SQLReady {
	Object[] args;
	String sql = null;
	public SQLReady(String sql){
		this(sql,null);
	}
	/**
	 * @param sql 带”？“的 sql语句
	 * @param args 参数
	 */
	public SQLReady(String sql,Object... args){
		this.sql = sql ;
		if(args==null){
			this.args = new Object[0];
		}else{
			this.args = args;
		}
		
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
