package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.AssignID;

public class Party  {
	private Integer id1 ;
	private Integer id2 ;
	private String name ;
	
	@AssignID(algorithm="simple")
	public Integer getId1() {
		return id1;
	}
	public void setId1(Integer id1) {
		this.id1 = id1;
	}
	@AssignID(algorithm="simple")
	public Integer getId2() {
		return id2;
	}
	public void setId2(Integer id2) {
		this.id2 = id2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}