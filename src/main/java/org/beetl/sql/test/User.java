package org.beetl.sql.test;
import java.util.Date;

import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.ColumnIgnore;

public class User extends TailBean  {
	private long userId ;
	private Integer age ;
	private Integer departmentId ;
	private String name ;
	private String userName ;
	private Date bir ;
	
	public User() {
	}
	
	@AssignID("simple")
	public long getUserId(){
		return  userId;
	}
	public void setUserId(long userid ){
		this.userId = userid;
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
	
	@ColumnIgnore(insert=true,update=false)
	public Date getBir(){
		return  bir;
	}
	public void setBir(Date bir ){
		this.bir = bir;
	}
	

}
