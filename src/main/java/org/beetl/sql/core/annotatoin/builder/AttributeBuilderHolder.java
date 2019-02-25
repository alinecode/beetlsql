package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.annotatoin.Builder;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeBuilderHolder {

    Annotation beanAnnotaton;
    Object instance;

    static  Map<Class, Object> propertyHandlerMap = new ConcurrentHashMap<Class, Object>();
    public  Object newInstance(Class propertyHandlerClz){
        if(propertyHandlerMap.containsKey(propertyHandlerClz)){
            return  propertyHandlerMap.get(propertyHandlerClz);
        }

        Object propertyHanlder =   BeanKit.newInstance(propertyHandlerClz);

        propertyHandlerMap.put(propertyHandlerClz,propertyHanlder);
        return propertyHanlder;
    }



    public AttributeBuilderHolder(Annotation beanAnnotaton, Builder builderAnotation) {
        this.beanAnnotaton = beanAnnotaton;
        this.instance =newInstance(builderAnotation.value());
    }

    public Annotation getBeanAnnotaton() {
        return beanAnnotaton;
    }

   

    public Object getInstance() {
        return instance;
    }

    public boolean supportPersistGen(){
        return this.instance instanceof AttributePersistBuilder;
    }

    public boolean supportSelectMapping(){
        return  this.instance instanceof AttributeSelectBuilder;
    }
}
