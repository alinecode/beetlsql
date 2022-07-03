package org.beetl.sql.core.call;

import lombok.Data;

@Data
public class CallArg {
	private int index;
	protected int jdbcType = Integer.MAX_VALUE;

	public boolean hasJdbcType() {
		return jdbcType != Integer.MAX_VALUE;
	}
}
