package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLScript;

public interface ObjectPersistBuilder {
	public void beforePersist(Object entity,SQLScript sqlScript);
	 public void afterPersist(Object entity,SQLScript sqlScript);
}
