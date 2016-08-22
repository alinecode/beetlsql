package org.beetl.sql.test;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.TableTemplate;
import org.beetl.sql.core.annotatoin.Tail;
/*
* 
* gen by beetsql 2015-12-11
*/
//@TableTemplate()
@Tail(set="addValue")
public class User  {
	private Integer userid ;
	//å¹´çºª123
	private Integer age ;
	private Integer departmentId ;
	private String name ;
	private String userName ;
	//ç”Ÿæ—¥
	private Date bir ;
	
	public User() {
	}
	
	public Integer getUserid(){
		return  userid;
	}
	public void setUserid(Integer userid ){
		this.userid = userid;
	}
	
	public Integer getAge(){
		return  age;
	}
	public void setAge(Integer age ){
		this.age = age;
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
	
	public String getUserName(){
		return  userName;
	}
	public void setUserName(String userName ){
		this.userName = userName;
	}
	
	public Date getBir(){
		return  bir;
	}
	public void setBir(Date bir ){
		this.bir = bir;
	}
	

}
