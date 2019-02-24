package org.beetl.sql.core.annotatoin.builder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;

public class BaseObjectBuilder implements ObjectPersistBuilder,ObjectSelectBuilder {

	@Override
    public void beforePersist(Object entity,SQLManager sqlManager){

    }
	@Override
    public void afterPersist(Object entity,SQLManager sqlManager){

    }
	
	
	@Override
    public List<Object> afterSelect(Class target,List<Object> entitys,SQLManager sqlManager,Annotation beanAnnotaton,SQLResult sqlResult){
        return entitys;
    }
	@Override
	public void beforeSelect(Class target, SQLManager sqlManager, Annotation beanAnnotaton, Map<String, Object> paras) {
		// TODO Auto-generated method stub
		
	}
}
