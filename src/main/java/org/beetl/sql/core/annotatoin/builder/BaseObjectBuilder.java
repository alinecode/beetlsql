package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLScript;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public class BaseObjectBuilder implements ObjectPersistBuilder, ObjectSelectBuilder {


	@Override
	public void beforePersist(Object entity, SQLScript sqlScript) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterPersist(Object entity, SQLScript sqlScript) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeSelect(Class target, SQLScript sqlScript, Annotation beanAnnotaton, Map<String, Object> paras) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Object> afterSelect(Class target, List<Object> entitys, SQLScript sqlScript, Annotation beanAnnotaton,
			SQLResult sqlResult) {
		// TODO Auto-generated method stub
		return entitys;
	}


}
