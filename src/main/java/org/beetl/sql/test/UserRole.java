package org.beetl.sql.test;
import java.math.*;
import java.util.Date;
import java.sql.Timestamp;

/*
* 
* gen by beetlsql 2016-08-20
*/
public class UserRole  {
	
	private Integer userId ;
	private Integer roleId ;
	
	public UserRole() {
	}
	
	public Integer getUserId(){
		return  userId;
	}
	public void setUserId(Integer userId ){
		this.userId = userId;
	}
	
	public Integer getRoleId(){
		return  roleId;
	}
	public void setRoleId(Integer roleId ){
		this.roleId = roleId;
	}
	

}
