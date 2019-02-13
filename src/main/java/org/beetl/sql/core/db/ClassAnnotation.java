package org.beetl.sql.core.db;

import org.beetl.sql.core.annotatoin.InsertIgnore;
import org.beetl.sql.core.kit.BeanKit;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * 包含了class及其属性的注解
 */
public class ClassAnnotation {
    Class c;
    public ClassAnnotation(Class c){
        this.c = c ;
        init();
    }

    private void init(){

    }
    public PropertyDescriptor[] getPropertyDescriptor(){
        try {
            return BeanKit.propertyDescriptors(c);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public InsertIgnore getInsertIgnore(String property){
        return null;
    }
}
