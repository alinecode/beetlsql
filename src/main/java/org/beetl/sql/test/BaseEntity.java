package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.Column;
import org.beetl.sql.core.annotatoin.UpdateTime;

import java.time.LocalDateTime;

public class BaseEntity extends BaseObject {
	@Column("name")
	private String userName;

	@UpdateTime
	private LocalDateTime createTime;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
}
