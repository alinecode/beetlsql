package org.beetl.sql.core.call;

import lombok.Data;

@Data
public class CallArg {
	private int index;
	protected Integer jdbcType = null;

	public boolean hasJdbcType() {
		return jdbcType != null;
	}
}
