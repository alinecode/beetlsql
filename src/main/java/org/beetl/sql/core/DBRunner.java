package org.beetl.sql.core;

/** 强制 sql 选择主从执行
 * @author xiandafu
 *
 */
public abstract class DBRunner {
	public void start(SQLManager sm,boolean isMaster){
		sm.getDs().forceBegin(isMaster);
		run(sm);
		sm.getDs().forceEnd();
	}
	
	abstract public void run(SQLManager sm);
}
