package org.beetl.sql.test.mysql.entity;

import org.beetl.sql.core.annotatoin.AssignID;

public class Role  {
	private Integer id ;
	private String name ;
	
	public Role() {
	}
	
	@AssignID
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public String getName(){
		return  name;
	}
	public void setName(String name ){
		this.name = name;
	}
	

}