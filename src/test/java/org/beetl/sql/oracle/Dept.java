package org.beetl.sql.oracle;

import org.beetl.sql.core.annotatoin.SeqID;

public class Dept {
	Integer id;
	String name;
	Integer age;
	@SeqID(name="userSeq")
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
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "Dept [id=" + id + ", name=" + name + "]";
	}
	
}
