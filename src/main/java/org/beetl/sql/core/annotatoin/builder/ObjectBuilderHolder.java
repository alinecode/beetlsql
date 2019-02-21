package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.annotatoin.Builder;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectBuilderHolder {
    Annotation beanAnnotaton;
    Builder builderAnotation;
    BaseObjectBuilder instance;
    static Map<Class, BaseObjectBuilder> classHandlerMap = new ConcurrentHashMap<Class, BaseObjectBuilder>();

    public ObjectBuilderHolder(Annotation beanAnnotaton, Builder builderAnotation) {
        this.beanAnnotaton = beanAnnotaton;
        this.builderAnotation = builderAnotation;
        this.instance =newInstance(builderAnotation.value());
    }

    public  BaseObjectBuilder newInstance(Class objectHandlerClz){
        if(classHandlerMap.containsKey(objectHandlerClz)){
            return  classHandlerMap.get(objectHandlerClz);
        }

        BaseObjectBuilder objectHanlder =  (BaseObjectBuilder) BeanKit.newInstance(objectHandlerClz);
        classHandlerMap.put(objectHandlerClz,objectHanlder);
        return objectHanlder;
    }

    public Annotation getBeanAnnotaton() {
        return beanAnnotaton;
    }

    public void setBeanAnnotaton(Annotation beanAnnotaton) {
        this.beanAnnotaton = beanAnnotaton;
    }

    public Builder getBuilderAnotation() {
        return builderAnotation;
    }

    public void setBuilderAnotation(Builder builderAnotation) {
        this.builderAnotation = builderAnotation;
    }

    public BaseObjectBuilder getInstance() {
        return instance;
    }

    public void setInstance(BaseObjectBuilder instance) {
        this.instance = instance;
    }
}
