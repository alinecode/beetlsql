package org.beetl.sql.core.call;

import lombok.Data;

/**
 * 存储过程入参
 */
@Data
public class InArg extends CallArg {
	private Object arg;

	public InArg(Object arg) {
		this.arg = arg;
	}

	public InArg(Object arg, int jdbcType) {
		this.arg = arg;
		this.jdbcType = jdbcType;
	}
}
