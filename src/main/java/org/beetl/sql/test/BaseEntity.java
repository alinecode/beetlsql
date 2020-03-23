package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.UpdateTime;

import java.time.LocalDateTime;

public class BaseEntity extends BaseObject {
	private String name;

	@UpdateTime
	private LocalDateTime createTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
}
