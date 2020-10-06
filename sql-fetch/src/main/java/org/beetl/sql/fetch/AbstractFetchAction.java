package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLManager;

import java.beans.PropertyDescriptor;

public abstract  class AbstractFetchAction  implements  FetchAction{

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

}
