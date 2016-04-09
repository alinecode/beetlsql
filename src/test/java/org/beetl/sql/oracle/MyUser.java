package org.beetl.sql.oracle;

import org.beetl.sql.core.annotatoin.SeqID;

/*
* 
* gen by beetlsql 2016-04-09
*/
public class MyUser  {
	private Long id ;
	private Integer age ;
	private String name ;
	@SeqID(name="my_user_seq")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}