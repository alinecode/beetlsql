package org.beetl.sql.core.engine;

import org.beetl.core.Format;
import org.beetl.sql.core.JavaType;

public class JdbcTypeFormat implements Format {

	@Override
	public Object format(Object data, String pattern) {
		char cs = pattern.charAt(0);
		if(cs=='-'||Character.isDigit(cs)) {
			return Integer.parseInt(pattern);
		}else {
			Integer type = JavaType.jdbcTypeNames.get(pattern.toLowerCase());
			
			return type;
			
		}
	}

}
