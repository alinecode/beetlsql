package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.SQLManager;

public interface ObjectPersistBuilder {
	public void beforePersist(Object entity,SQLManager sqlManager);
	 public void afterPersist(Object entity,SQLManager sqlManager);
}
