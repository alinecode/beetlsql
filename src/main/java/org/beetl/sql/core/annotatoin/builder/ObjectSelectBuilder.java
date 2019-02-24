package org.beetl.sql.core.annotatoin.builder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;

public interface ObjectSelectBuilder {
	 public void beforeSelect(Class target,SQLManager sqlManager,Annotation beanAnnotaton,Map<String, Object> paras) ;
	 public List<Object> afterSelect(Class target,List<Object> entitys,SQLManager sqlManager,Annotation beanAnnotaton,SQLResult sqlResult);
}
