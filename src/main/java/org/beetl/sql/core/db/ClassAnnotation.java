package org.beetl.sql.core.db;

import org.beetl.sql.core.annotatoin.*;
import org.beetl.sql.core.handler.AttributeHanlderHolder;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.CaseInsensitiveHashMap;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记录了class及其属性的所有注解
 */
public class ClassAnnotation {
    static Map<Class,ClassAnnotation> cache = new ConcurrentHashMap<Class,ClassAnnotation>();
    Class entity = null;
    //update和insert 忽略策略
    Map<String, ClassDesc.ColumnIgnoreStatus> attrIgnores = new HashMap<String, ClassDesc.ColumnIgnoreStatus>();
    // 逻辑删除标记
    String logicDeleteAttrName =null;
    int logicDeleteAttrValue = 0;
    //版本号标记
    String versionProperty;
    int initVersionValue = -1;

    CaseInsensitiveHashMap<String,AttributeHanlderHolder> colHandlers = new CaseInsensitiveHashMap<String,AttributeHanlderHolder>();

    public static ClassAnnotation getClassAnnotation(Class entity){
        ClassAnnotation ca = cache.get(entity);
        if(ca!=null){
            return ca;
        }
        ca = new ClassAnnotation(entity);
        ca.init();
        cache.put(entity,ca);
        return ca;
    }

    protected  ClassAnnotation(Class entity){
        this.entity = entity ;

    }

    protected void init(){
        PropertyDescriptor[] ps = this.getPropertyDescriptor();
        for(PropertyDescriptor p:ps){
            Method readMethod =  p.getReadMethod();
            //各种内置注解
            ColumnIgnore sqlIgnore = BeanKit.getAnnoation(entity, p.getName(), readMethod, ColumnIgnore.class);
            if(sqlIgnore!=null){
                attrIgnores.put(p.getName(), new ClassDesc.ColumnIgnoreStatus(sqlIgnore));
            }else{

                InsertIgnore ig = BeanKit.getAnnoation(entity, p.getName(), readMethod, InsertIgnore.class);
                UpdateIgnore ug = BeanKit.getAnnoation(entity, p.getName(), readMethod, UpdateIgnore.class);
                if(ig!=null||ug!=null){
                    attrIgnores.put(p.getName(), new ClassDesc.ColumnIgnoreStatus(ig,ug));
                }
            }

            LogicDelete logicDelete =  BeanKit.getAnnoation(entity, p.getName(), readMethod, LogicDelete.class);
            if(logicDelete!=null) {
                this.logicDeleteAttrName = p.getName();
                this.logicDeleteAttrValue =logicDelete.value();
            }


            Version version =  BeanKit.getAnnoation(entity, p.getName(), readMethod, Version.class);
            if(version!=null){
                this.versionProperty = p.getName();
                this.initVersionValue =version.value();
            }

            AttributeHanlderHolder holder = BeanKit.getAttributeHanlderHolder(entity,p.getName(),p);
            if(holder!=null){
                //判断是否有对字段特殊处理
                colHandlers.put(p.getName(),holder);
            }


        }
    }

    public PropertyDescriptor[] getPropertyDescriptor(){
        try {
            return BeanKit.propertyDescriptors(entity);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public InsertIgnore getInsertIgnore(String property){
        return null;
    }

    public CaseInsensitiveHashMap<String, AttributeHanlderHolder> getColHandlers() {
        return colHandlers;
    }

    public Class getEntity() {
        return entity;
    }

    public Map<String, ClassDesc.ColumnIgnoreStatus> getAttrIgnores() {
        return attrIgnores;
    }

    public String getLogicDeleteAttrName() {
        return logicDeleteAttrName;
    }

    public int getLogicDeleteAttrValue() {
        return logicDeleteAttrValue;
    }

    public String getVersionProperty() {
        return versionProperty;
    }

    public int getInitVersionValue() {
        return initVersionValue;
    }
}
