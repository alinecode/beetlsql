package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.AssignID;

public class Party  {
	private String id="abc" ;
	private String name ;
	@AssignID("uuid2")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}