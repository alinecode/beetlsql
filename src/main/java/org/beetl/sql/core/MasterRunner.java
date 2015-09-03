package org.beetl.sql.core;

public class MasterRunner {
	public void start(SQLManager sm){
		sm.getDs().onlyMasterBegin();
		run(sm);
		sm.getDs().onlyMasterEnd();
	}
	
	protected void run(SQLManager sm){
		
	}
}
