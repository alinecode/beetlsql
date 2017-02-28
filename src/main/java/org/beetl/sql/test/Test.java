package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.ColumnIgnore;

public class Test {
	Integer id;
	Integer name;
	
	@AutoID
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ColumnIgnore(insert=true)
	public Integer getName() {
		return name;
	}
	public void setName(Integer name) {
		this.name = name;
	}
	
}
