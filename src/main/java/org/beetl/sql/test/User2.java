package org.beetl.sql.test;

import java.math.BigDecimal;

import org.beetl.sql.core.annotatoin.Table;

@Table(name="test2.user2")
public class User2  {
	private Long id ;

	private String username ;
	
	BigDecimal salary;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public BigDecimal getSalary() {
		return salary;
	}
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	

}
