package org.beetl.sql.test.mysql.entity;

import org.beetl.sql.core.TailBean;

public class User  extends TailBean{
	private Integer id ;
	private Integer departmentId ;
	private String name ;
	
	public User() {
	}
	
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public Integer getDepartmentId(){
		return  departmentId;
	}
	public void setDepartmentId(Integer departmentId ){
		this.departmentId = departmentId;
	}
	
	public String getName(){
		return  name;
	}
	public void setName(String name ){
		this.name = name;
	}
	

}