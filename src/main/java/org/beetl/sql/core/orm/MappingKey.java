package org.beetl.sql.core.orm;

import org.beetl.sql.core.SQLManager;

public interface MappingKey {
	public Object getValue(Object o,SQLManager sm);
}
