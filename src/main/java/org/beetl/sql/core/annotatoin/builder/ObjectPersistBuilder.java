package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.SQLScript;

public interface ObjectPersistBuilder {
	void beforePersist(Object entity, SQLScript sqlScript);

	void afterPersist(Object entity, SQLScript sqlScript);
}
