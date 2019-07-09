package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLScript;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface ObjectSelectBuilder {
	void beforeSelect(Class target, SQLScript sqlScript, Annotation beanAnnotaton, Map<String, Object> paras);

	List<Object> afterSelect(Class target, List<Object> entitys, SQLScript sqlScript, Annotation beanAnnotaton,
			SQLResult sqlResult);
}
