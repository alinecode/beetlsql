package org.beetl.sql.oracle;

import java.math.*;
import java.util.Date;
import java.sql.Timestamp;

/*
* 
* gen by beetlsql 2016-08-02
*/
public class SysUser2  {
	private String id ;
	private Integer gender ;
	private Double money ;
	private String password ;
	private String userName ;
	private Timestamp bir ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Timestamp getBir() {
		return bir;
	}
	public void setBir(Timestamp bir) {
		this.bir = bir;
	}

}