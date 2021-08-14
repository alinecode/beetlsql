package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLManager;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 实现FetchAction
 * @author xiandafu
 */
public abstract  class AbstractFetchAction  implements  FetchAction{

	Annotation annotation ;
	PropertyDescriptor originProperty;
	Class owner;
	Class target;

    public Object queryFromCache(Class target,Object key){
        FetchContext context  = DefaultBeanFetch.local.get();
        Object cached = context.find(target,key);
        return cached;
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
            Object key  = this.wrapId(sqlManager,value);
            return queryFromCache(target,key);
        } catch (Exception e) {
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,e);
        }
    }

    public void addCached(SQLManager sqlManager,Object obj){
        Object key  = wrapId(sqlManager,obj);
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

	protected Object wrapId(SQLManager sqlManager,Object obj){
		Class target = obj.getClass();
		List<String> list  = sqlManager.getClassDesc(target).getIdAttrs();
		if(list.size()==0){
			Object key  = BeanKit.getBeanProperty(obj,list.get(0));
			return key;
		}
		//复合主键
		Ids ids = new Ids();
		for(String attr:list){
			Object key  = BeanKit.getBeanProperty(obj,attr);
			ids.keys.add(key);
		}

		return ids;

	}

	static class Ids {
		public List<Object> keys = new ArrayList<>();

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Ids ids = (Ids) o;
			return Objects.equals(keys, ids.keys);
		}

		@Override
		public int hashCode() {
			return Objects.hash(keys);
		}
	}
}
