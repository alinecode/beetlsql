package org.beetl.sql.postgres;

import org.beetl.sql.core.annotatoin.Table;

//不支持指定别的schema
@Table(name="bk.user")
public class User {
	public Integer id;
	public Integer age ;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
}
