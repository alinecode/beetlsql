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
	private Integer userId ;
	private Integer age ;
	
	private String name ;
	//用户名称
	private String userName ;

	private Integer departmentId ;
	
	Map<String,Object> ext = new HashMap<String,Object>();
	
	//for query
	private Date minDate;
	private Date maxDate;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@AssignID
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
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
	
	public Integer getDepartmentId(){
		return  departmentId;
	}
	public void setDepartmentId(Integer departmentId ){
		this.departmentId = departmentId;
	}
	
	
	public User addValue(String str,Object ok){
		ext.put(str, ok);
		return this;
	}
	public Map<String, Object> getExt() {
		return ext;
	}
	public void setExt(Map<String, Object> ext) {
		this.ext = ext;
	}
	

}
