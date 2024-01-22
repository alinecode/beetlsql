package com.ibeetl.sql.dynamic;


import com.beetl.sql.dynamic.BaseEntity;
import org.beetl.core.fun.MethodInvoker;
import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.annotation.entity.AssignID;

/**
 * 用户自定义对象
 */
public  class Office  extends BaseEntity {

	@AssignID
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
