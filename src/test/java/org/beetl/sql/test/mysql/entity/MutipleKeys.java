package org.beetl.sql.test.mysql.entity;

import org.beetl.sql.core.annotatoin.AssignID;

/*
* 
* gen by beetlsql 2016-09-09
*/
public class MutipleKeys  {
	
	private Integer gender ;
	private String userName ;
	private String description ;
	
	public MutipleKeys() {
	}
	@AssignID
	public Integer getGender(){
		return  gender;
	}
	public void setGender(Integer gender ){
		this.gender = gender;
	}
	
	@AssignID
	public String getUserName(){
		return  userName;
	}
	public void setUserName(String userName ){
		this.userName = userName;
	}
	
	public String getDescription(){
		return  description;
	}
	public void setDescription(String description ){
		this.description = description;
	}
	

}