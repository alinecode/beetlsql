package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLManager;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;

/**
 * 实现FetchAction
 * @author xiandafu
 */
public abstract  class AbstractFetchAction  implements  FetchAction{

	Annotation annotation ;
	PropertyDescriptor originProperty;
	Class owner;
	Class target;
	String enableOn ;

    public Object queryFromCache(Class target,Object key){
        FetchContext context  = DefaultBeanFetch.local.get();
        Object cached = context.find(target,key);
        return cached;
    }

    public Object queryFromCache(Class target, Object value,PropertyDescriptor idProperty){
        try {
            Object key = idProperty.getReadMethod().invoke(value,new Object[0]);
            return queryFromCache(target,key);
        } catch (Exception e) {
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,e);
        }
    }

	/**
	 * 判断某个对象是否已经加载
	 * @param sqlManager
	 * @param value
	 * @return
	 */
    public Object queryFromCache(SQLManager sqlManager,Object value){
        try {
            Class target = value.getClass();
            String attr = sqlManager.getClassDesc(target).getIdAttr();
            Object key  = BeanKit.getBeanProperty(value,attr);
            return queryFromCache(target,key);
        } catch (Exception e) {
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,e);
        }
    }

    public void addCached(SQLManager sqlManager,Object obj){
        Class target = obj.getClass();
        String attr = sqlManager.getClassDesc(target).getIdAttr();
        Object key  = BeanKit.getBeanProperty(obj,attr);
        addCached(obj,key);
    }
    public void addCached(Object value,Object key){
        FetchContext context  = DefaultBeanFetch.local.get();
        context.add(value.getClass(),key,value);
    }

	public void addAttribute(Object obj,String attrName){
		FetchContext context  = DefaultBeanFetch.local.get();
		context.addAttribute(obj,attrName);
	}

	/**
	 * 判断对象的某个属性是否已经加载
	 * @param obj
	 * @param attrName
	 * @return
	 */
	public  boolean containAttribute(Object obj,String attrName){
		FetchContext context  = DefaultBeanFetch.local.get();
		return context.containAttribute(obj,attrName);
	}

	@Override
	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public void init(Class owner, Class target,Annotation config, PropertyDescriptor originProperty){
    	this.owner = owner;
    	this.target = target;
    	this.annotation = config;
    	this.originProperty = originProperty;


	}

	@Override
	public PropertyDescriptor getOriginProperty(){
    	return this.originProperty;
	}
}
