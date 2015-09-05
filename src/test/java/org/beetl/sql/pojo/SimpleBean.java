package org.beetl.sql.pojo;

import org.beetl.sql.core.Bean2;
import org.beetl.sql.core.annotatoin.Table;

@Table(name="user")
public class SimpleBean extends Bean2 {
	public int id ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
