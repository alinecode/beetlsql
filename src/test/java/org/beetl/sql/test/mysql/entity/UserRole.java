package org.beetl.sql.test.mysql.entity;

public class UserRole  {
	private Integer roleId ;
	private Integer userId ;
	
	public UserRole() {
	}
	
	public Integer getRoleId(){
		return  roleId;
	}
	public void setRoleId(Integer roleId ){
		this.roleId = roleId;
	}
	
	public Integer getUserId(){
		return  userId;
	}
	public void setUserId(Integer userId ){
		this.userId = userId;
	}
	

}