package org.beetl.sql.postgres;

import java.util.Date;

import org.beetl.sql.core.annotatoin.AutoID;

public class Company2 {
	Integer id;
	String name;
	Date date;
	@AutoID
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "Company2 [id=" + id + ", name=" + name + "]";
	}
	
	
}
