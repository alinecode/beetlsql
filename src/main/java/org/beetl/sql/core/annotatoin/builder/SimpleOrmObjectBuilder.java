package org.beetl.sql.core.annotatoin.builder;

public class SimpleOrmObjectBuilder extends BaseObjectBuilder{
    public Object afterSelect(Object entity){
        return entity;
    }
}
