package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;


public class BaseObject {

	@AssignID("simple")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
