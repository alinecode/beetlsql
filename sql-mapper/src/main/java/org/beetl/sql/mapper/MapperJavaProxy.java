package org.beetl.sql.mapper;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.builder.BaseMapperConfigBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java代理实现.
 * <p>
 * <a href="http://git.oschina.net/xiandafu/beetlsql/issues/54"># 54</a>
 * 封装sqlmanager
 * </p>
 *
 * @author zhoupan, xiandafu
 */
public class MapperJavaProxy implements InvocationHandler {

    /**
     * The sql manager.
     */
    protected SQLManager sqlManager;

    /**
     * The entity class.
     */
    protected Class<?> entityClass;


    protected BaseMapperConfigBuilder builder;



    protected Class mapperInterface;


    private static final Map<Class, Object> PROVIDERS_CACHE = new ConcurrentHashMap<Class, Object>();

    /**
     * The Constructor.
     */
    public MapperJavaProxy() {

    }

    /**
     * @param builder
     * @param sqlManager
     * @param mapperInterface
     */
    public MapperJavaProxy(BaseMapperConfigBuilder builder, SQLManager sqlManager, Class<?> mapperInterface) {
        super();
        this.sqlManager = sqlManager;
        this.builder = builder;
        this.mapperInterface(mapperInterface);
        this.mapperInterface = mapperInterface;
    }


    /**
     * Mapper interface.
     *
     * @param mapperInterface the dao2 interface
     * @return the dao2 proxy
     */
    public MapperJavaProxy mapperInterface(Class<?> mapperInterface) {
        this.onResolveEntityClassFromMapperInterface(mapperInterface);
        return this;
    }


    /**
     * Entity class.
     *
     * @param entityClass the entity class
     * @return the dao2 proxy
     */
    public MapperJavaProxy entityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    /**
     * Check args.
     */
    protected void checkArgs() {
    }

    /**
     * Builds the.
     *
     * @return the dao2 proxy
     */
    public MapperJavaProxy build() {
        this.checkArgs();
        return this;
    }

    /**
     * 获取BaseMapper&lt;EntityClass&gt;接口的泛型实体参数类.
     *
     * @param mapperInterface the dao2 interface
     */
    protected void onResolveEntityClassFromMapperInterface(Class<?> mapperInterface) {
        if (mapperInterface.isInterface()) {
            this.entityClass = BeanKit.getMapperEntity(mapperInterface);
        } else {
            throw new IllegalArgumentException("mapperInterface is not interface.");
        }
    }


    /**
     * 获得 Invoke.
     * @param proxy  the proxy
     * @param method the method
     * @param args   the args
     * @return the object
     * @throws Throwable the throwable
     */
    //Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class caller = method.getDeclaringClass();
        MapperInvoke invoke = builder.getAmi(entityClass,caller,method);
        Object ret = invoke.call(this.sqlManager, this.entityClass, method, args);
        return ret;
    }



}
