package org.beetl.sql.core.handler;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.Handler;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeHanlderHolder {

    Annotation beanAnnotaton;
    Handler handlerAnotation;
    BeanHandler instance;

    Map<Class, BeanHandler> propertyHandlerMap = new ConcurrentHashMap<Class, BeanHandler>();
    public synchronized  BeanHandler newInstance(Class propertyHandlerClz){
        if(propertyHandlerMap.containsKey(propertyHandlerClz)){
            return  propertyHandlerMap.get(propertyHandlerClz);
        }

        BeanHandler propertyHanlder =  (BeanHandler) BeanKit.newInstance(propertyHandlerClz);

        propertyHandlerMap.put(propertyHandlerClz,propertyHanlder);
        return propertyHanlder;
    }



    public AttributeHanlderHolder(Annotation beanAnnotaton, Handler handlerAnotation) {
        this.beanAnnotaton = beanAnnotaton;
        this.handlerAnotation = handlerAnotation;
        this.instance =newInstance(handlerAnotation.value());
    }

    public Annotation getBeanAnnotaton() {
        return beanAnnotaton;
    }

    public Handler getHandlerAnotation() {
        return handlerAnotation;
    }

    public BeanHandler getInstance() {
        return instance;
    }

    public boolean supportPersistGen(){
        return this.handlerAnotation.persist();
    }

    public boolean supportSelectMapping(){
        return this.handlerAnotation.select();
    }
}
