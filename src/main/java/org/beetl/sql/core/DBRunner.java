package org.beetl.sql.core;

public abstract class DBRunner {
	public void start(SQLManager sm,boolean isMaster){
		sm.getDs().forceBegin(isMaster);
		run(sm);
		sm.getDs().forceEnd();
	}
	
	abstract public void run(SQLManager sm);
}
