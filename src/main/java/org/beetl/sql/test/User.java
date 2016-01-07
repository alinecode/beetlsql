package org.beetl.sql.test;
import java.util.Date;

import org.beetl.sql.core.annotatoin.DateTemplate;
import org.beetl.sql.core.annotatoin.TableTemplate;
/*
* 
* gen by beetsql 2015-12-11
*/
@TableTemplate()
public class User  {
	private Integer id ;
	private Integer age ;
	//用户角色
	private Integer roleId ;
	private String name ;
	//用户名称
	private String userName ;
	private Date date ;
	
	//for query
	private Date minDate;
	private Date maxDate;
	
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	@DateTemplate()
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getMinDate() {
		return minDate;
	}
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	public Date getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
	
	

}
