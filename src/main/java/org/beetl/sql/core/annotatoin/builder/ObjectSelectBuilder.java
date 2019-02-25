package org.beetl.sql.core.annotatoin.builder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLScript;

public interface ObjectSelectBuilder {
	 public void beforeSelect(Class target,SQLScript sqlScript,Annotation beanAnnotaton,Map<String, Object> paras) ;
	 public List<Object> afterSelect(Class target,List<Object> entitys,SQLScript sqlScript,Annotation beanAnnotaton,SQLResult sqlResult);
}
