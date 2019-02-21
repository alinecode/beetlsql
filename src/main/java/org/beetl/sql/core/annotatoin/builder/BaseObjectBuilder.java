package org.beetl.sql.core.annotatoin.builder;


public class BaseObjectBuilder {
    public void beforePersist(Object entity){

    }

    public Object afterSelect(Object entity){
        return entity;
    }
}
