package org.beetl.sql.mapper;

import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.builder.MapperConfigBuilder;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Java代理实现.
 *
 * @author zhoupan, xiandafu
 */
public class MapperJava8Proxy extends  MapperJavaProxy {

    /** 避免每次调用都反射创建 */
    protected MethodHandles.Lookup lookup;
    
    public MapperJava8Proxy() {

    }

    /**
     * @param builder
     * @param sqlManager
     * @param mapperInterface
     */
    public MapperJava8Proxy(MapperConfigBuilder builder, SQLManager sqlManager, Class<?> mapperInterface) {
        super(builder,sqlManager,mapperInterface);
    }


    /**
     * Mapper interface.
     *
     * @param mapperInterface the dao2 interface
     * @return the dao2 proxy
     */
    @Override
    public MapperJava8Proxy mapperInterface(Class<?> mapperInterface) {
        super.mapperInterface(mapperInterface);
        return this;
    }


    /**
     * Entity class.
     *
     * @param entityClass the entity class
     * @return the dao2 proxy
     */
    @Override
    public MapperJava8Proxy entityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

 
    /**
     * Builds the.
     *
     * @return the dao2 proxy
     */
    @Override
    public MapperJava8Proxy build() {
        super.build();
        return this;
    }

  
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class caller = method.getDeclaringClass();
        if(caller==java.lang.Object.class){
            return method.invoke(this,args);
        }

        if(args==null){
            args = new Object[0];
        }

        if (method.isDefault()) {
			/*java8以后的接口允许default实现，使用者提供了默认实现的情况下直接调用*/
            return invokeDefaultMethod(proxy,method,args);

        }
        return super.invoke(proxy, method, args);


    }

    /**
     * 若jdk版本在1.8以上，且被调用的方法是默认方法，直接调用默认实现
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    protected  Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable{
        //https://dzone.com/articles/correct-reflective-access-to-interface-default-methods
        //https://gist.github.com/lukaseder/f47f5a0d156bf7b80b67da9d14422d4a
        //如何优化？
        if (JavaType.JAVA_MAJOR_VERSION <= 8) {
            final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);

            final Class<?> clazz = method.getDeclaringClass();
            return constructor.newInstance(clazz)
                    .in(clazz)
                    .unreflectSpecial(method, clazz)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        } else {
            //高于java8？
            return MethodHandles.lookup()
                    .findSpecial(
                            method.getDeclaringClass(),
                            method.getName(),
                            MethodType.methodType(method.getReturnType(), new Class[0]),
                            method.getDeclaringClass()
                    ).bindTo(proxy)
                    .invokeWithArguments(args);
        }
    }


    
 
    
  

}
