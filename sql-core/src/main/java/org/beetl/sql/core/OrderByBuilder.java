package org.beetl.sql.core;

import lombok.Data;
import org.beetl.core.fun.MethodInvoker;
import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.query.LambdaQuery;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 一个生成排序语句的工具类，相比于直接提供字符串，这个更适合重构，以及防止sql注入
 * @author xiandafu
 */
public class OrderByBuilder {
    SQLManager sqlManager = null;
    StringBuilder sb = new StringBuilder();
    public OrderByBuilder(SQLManager sqlManager){
        this.sqlManager = sqlManager;
    }

    static OrderByBuilder  from(SQLManager sqlManager){
        return new  OrderByBuilder(sqlManager);
    }

    public OrderByBuilder asc(Class clz,String name){
        checkProperty(clz,name);
        sb.append(name+" asc ");
        return this;
    }

    public OrderByBuilder asc(Class clz,String name,String prefix){
        checkProperty(clz,name);
        sb.append(prefix+"."+name+" asc ");
        return this;
    }

    public OrderByBuilder asc(Function name){
        return this;
    }

    public OrderByBuilder asc(LambdaQuery.Property name,String prefix){
        return this;
    }


    public OrderByBuilder desc(String name){
        return this;
    }



    protected String getFunctionName(LambdaQuery.Property property) {
        try {
            Method declaredMethod = property.getClass().getDeclaredMethod("writeReplace");
            declaredMethod.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) declaredMethod.invoke(property);
            String method = serializedLambda.getImplMethodName();
            String attr = null;
            if (method.startsWith("get")) {
                attr = method.substring(3);
            } else {
                attr = method.substring(2);
            }

            String cls = serializedLambda.getImplClass();
            Class clazz = Class.forName(cls.replace('/','.'));;

            return sqlManager.getNc().getColName(clazz, StringKit.toLowerCaseFirstOne(attr));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

    }

    private void checkProperty(Class clz,String name){
        MethodInvoker inv = ObjectUtil.getInvokder(clz, name);
        if(inv==null){
            throw new IllegalArgumentException("属性不存在 "+clz+"."+name);
        }
    }



    public static void main(String[] args){
        new OrderByBuilder(null).test();

    }

    public void test(){
//        this.asc(User::getName);

    }

    @Data
    public  static class User {
        String name;
    }




}
