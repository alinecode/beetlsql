package org.beetl.sql.core.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.annotatoin.Builder;
import org.beetl.sql.core.annotatoin.ColumnIgnore;
import org.beetl.sql.core.annotatoin.InsertIgnore;
import org.beetl.sql.core.annotatoin.LogicDelete;
import org.beetl.sql.core.annotatoin.UpdateIgnore;
import org.beetl.sql.core.annotatoin.Version;
import org.beetl.sql.core.annotatoin.builder.AttributeBuilderHolder;
import org.beetl.sql.core.annotatoin.builder.ObjectBuilderHolder;
import org.beetl.sql.core.annotatoin.builder.ObjectPersistBuilder;
import org.beetl.sql.core.annotatoin.builder.ObjectSelectBuilder;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.CaseInsensitiveHashMap;

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

    //属性对应的处理类,TODO优化，设置为null，否则jdk6有一定损耗
    CaseInsensitiveHashMap<String, AttributeBuilderHolder> colHandlers = new CaseInsensitiveHashMap<String, AttributeBuilderHolder>();
    //类对应的处理类，可以多个
    List<ObjectBuilderHolder> objectBuilders = new ArrayList<ObjectBuilderHolder>();
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
        typeCheck();
        propertyCheck();

    }

    protected void typeCheck(){
        Annotation[] ans = this.entity.getAnnotations();
        List<ObjectBuilderHolder> list = new ArrayList<ObjectBuilderHolder>();
        for(Annotation an:ans){
            Builder builder = an.annotationType().getAnnotation(Builder.class);
            if(builder==null){
                continue;
            }
            Class clz = builder.value();
            Object obj = BeanKit.newInstance(clz);
            if(!(obj instanceof ObjectPersistBuilder || obj instanceof ObjectSelectBuilder )){
                throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,entity+" 的注解 "+an+"  的value值必须是 BaseObjectBuilder子类");
            }
            ObjectBuilderHolder holder = new ObjectBuilderHolder(an,builder);
            list.add(holder);
            
        }
        objectBuilders.addAll(list);
    }

    protected  void propertyCheck(){
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

            AttributeBuilderHolder holder = BeanKit.getAttributeHanlderHolder(entity,p.getName(),p);
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

    public CaseInsensitiveHashMap<String, AttributeBuilderHolder> getColHandlers() {
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

	public List<ObjectBuilderHolder> getObjectBuilders() {
		return objectBuilders;
	}

	public void setObjectBuilders(List<ObjectBuilderHolder> objectBuilders) {
		this.objectBuilders = objectBuilders;
	}

   
}
