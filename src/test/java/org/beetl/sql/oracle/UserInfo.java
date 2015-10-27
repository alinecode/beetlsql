package org.beetl.sql.oracle;

import javax.xml.bind.annotation.XmlInlineBinaryData;

import org.beetl.sql.core.annotatoin.AssignID;

public class UserInfo {
	private Integer id ;
	private String userName;
	@AssignID
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
