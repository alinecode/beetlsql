package org.beetl.sql.test;

import org.beetl.sql.core.TailBean;


public class User   extends TailBean {
	
	private Integer id ;
	private String name ;
	private Color gender;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Color getGender() {
		return gender;
	}
	public void setGender(Color gender) {
		this.gender = gender;
	}
	


}
