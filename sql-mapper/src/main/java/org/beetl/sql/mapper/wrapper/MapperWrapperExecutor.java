package org.beetl.sql.mapper.wrapper;



import org.beetl.sql.core.SQLManager;

import java.lang.annotation.Annotation;

public interface MapperWrapperExecutor {
	 void before(SQLManager sqlManager,Annotation annotation);
}
