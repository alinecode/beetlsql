package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.AssignID;


public class BaseObject {

	@AssignID("test")
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
