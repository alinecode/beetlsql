package org.beetl.sql.test;


import lombok.Data;
import org.beetl.sql.annotation.entity.*;

@Data
@Table(name="sys_user")
public class MyUser {
    @AssignID("uuid")
    private Integer id;
    private String name;

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
}
