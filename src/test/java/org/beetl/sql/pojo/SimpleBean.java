package org.beetl.sql.pojo;

import org.beetl.sql.core.QueryResultBean;
import org.beetl.sql.core.annotatoin.Table;

@Table(name="user")
public class SimpleBean extends QueryResultBean {
	public int id ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
