package org.beetl.sql.test;
import java.math.*;
import java.util.Date;

import org.beetl.sql.core.annotatoin.EnumMapping;

import java.sql.Timestamp;

/*
* 
* gen by beetlsql 2016-03-28
*/
public class Role  {
	
	@EnumMapping("value")
	public enum RoleType { 
		EMPLOYEE("EMPLOYEE",1),MANAGER ("MANAGER",2);
		
		private String name;
		private int value;
		
		RoleType(String name, int value) {  
		    this.name = name;  
		    this.value = value;  
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}  
		
		
	} 
	
	
	private Integer id ;
	RoleType type ;
	private String name ;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public RoleType getType() {
		return type;
	}
	public void setType(RoleType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}