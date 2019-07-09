package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class IsBlank implements Function {
	@Override
	public Boolean call(Object[] paras, Context ctx) {
		Object o = paras[0];
		if (o == null) {
			return true;
		}
		if (!(o instanceof String)) {
			throw new IllegalArgumentException("期望参数是String");
		}
		String cs = (String) o;
		return cs.trim().length() == 0;

	}
}