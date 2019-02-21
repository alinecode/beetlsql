package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.annotatoin.Builder;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeBuilderHolder {

    Annotation beanAnnotaton;
    Builder builderAnotation;
    BaseAttributeBuilder instance;

    static  Map<Class, BaseAttributeBuilder> propertyHandlerMap = new ConcurrentHashMap<Class, BaseAttributeBuilder>();
    public  BaseAttributeBuilder newInstance(Class propertyHandlerClz){
        if(propertyHandlerMap.containsKey(propertyHandlerClz)){
            return  propertyHandlerMap.get(propertyHandlerClz);
        }

        BaseAttributeBuilder propertyHanlder =  (BaseAttributeBuilder) BeanKit.newInstance(propertyHandlerClz);

        propertyHandlerMap.put(propertyHandlerClz,propertyHanlder);
        return propertyHanlder;
    }



    public AttributeBuilderHolder(Annotation beanAnnotaton, Builder builderAnotation) {
        this.beanAnnotaton = beanAnnotaton;
        this.builderAnotation = builderAnotation;
        this.instance =newInstance(builderAnotation.value());
    }

    public Annotation getBeanAnnotaton() {
        return beanAnnotaton;
    }

    public Builder getBuilderAnotation() {
        return builderAnotation;
    }

    public BaseAttributeBuilder getInstance() {
        return instance;
    }

    public boolean supportPersistGen(){
        return this.builderAnotation.persist();
    }

    public boolean supportSelectMapping(){
        return this.builderAnotation.select();
    }
}
