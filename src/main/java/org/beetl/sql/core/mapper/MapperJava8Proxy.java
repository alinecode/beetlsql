package org.beetl.sql.core.mapper;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.mapper.builder.MapperConfig;
import org.beetl.sql.core.mapper.builder.MapperInvokeDataConfig;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;

/**
 * Java代理实现.
 * <p>
 * <a href="http://git.oschina.net/xiandafu/beetlsql/issues/54"># 54</a>
 * 封装sqlmanager
 * </p>
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
    public MapperJava8Proxy(DefaultMapperBuilder builder, SQLManager sqlManager, Class<?> mapperInterface) {
        super(builder,sqlManager,mapperInterface);
   
    }


    /**
     * Mapper interface.
     *
     * @param mapperInterface the dao2 interface
     * @return the dao2 proxy
     */
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
    public MapperJava8Proxy entityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

 
    /**
     * Builds the.
     *
     * @return the dao2 proxy
     */
    public MapperJava8Proxy build() {
        super.build();
        return this;
    }

  
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class caller = method.getDeclaringClass();

        // 若jdk版本在1.8以上，且被调用的方法是默认方法，直接调用默认实现
        if (method.isDefault()) {
            // 每一个dao都只会创建一次lookup（若并发，多创建几次也没什么问题，要么if里用同步块做二次检查也可）
            if (lookup == null) {
                // 若要效率更高，可以将constructor缓存下来，每个dao要创建lookup时都使用同一个constructor
                Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                constructor.setAccessible(true);
                lookup = constructor.newInstance(caller, MethodHandles.Lookup.PRIVATE);
            }
            // 通过lookup直接调用默认实现
            return lookup.unreflectSpecial(method, caller).bindTo(proxy).invokeWithArguments(args);
        }else {
            return super.invoke(proxy, method, args);
        }

      


    }
    
 
    
  

}
