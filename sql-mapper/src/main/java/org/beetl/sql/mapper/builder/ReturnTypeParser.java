package org.beetl.sql.mapper.builder;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.page.PageResult;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class  ReturnTypeParser {
    Method method;
    Type type ;
    Class target ;
    Class    defaultRetType;
    public ReturnTypeParser(Method method, Class  defaultRetType){
        this.method = method;
        this.type = method.getGenericReturnType();
        this.target = method.getReturnType();
        this.defaultRetType = defaultRetType;
    }

    public Class getType(){
        return target;
    }

    public boolean isPageResult(){
        return PageResult.class.isAssignableFrom(target);
    }

    public boolean isCollection(){
        return Collection.class.isAssignableFrom(target);
    }


    public Class getCollectionType(){
        Class collectionType = BeanKit.getCollectionType(type);
        if(collectionType==null){
            return defaultRetType;
        }else{
            return collectionType;
        }

    }

    public Class getPageResultType(){
        //返回类型可能是List<XXX> 或者是 PageResult<XXX>
        if(!(type instanceof ParameterizedType) ){
            return defaultRetType;
        }
        Class paraType =  BeanKit.getParameterTypeClass(type);
        return paraType==null?defaultRetType:paraType;
    }



}