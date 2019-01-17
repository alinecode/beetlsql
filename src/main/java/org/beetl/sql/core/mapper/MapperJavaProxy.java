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
public class MapperJavaProxy implements InvocationHandler {

    /** The sql manager. */
    protected SQLManager sqlManager;

    /** The entity class. */
    protected Class<?> entityClass;


    protected DefaultMapperBuilder builder;


    protected MapperConfig mapperConfig;
    
    protected Class mapperInterface;

    /** 避免每次调用都反射创建 */
    private MethodHandles.Lookup lookup;

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
    public MapperJavaProxy(DefaultMapperBuilder builder, SQLManager sqlManager, Class<?> mapperInterface) {
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
            Type[] faces = mapperInterface.getGenericInterfaces();
            if (faces.length > 0 && faces[0] instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) faces[0];
                if (pt.getActualTypeArguments().length > 0) {
                    this.entityClass = (Class<?>) pt.getActualTypeArguments()[0];

                }
            }
        } else {
            throw new IllegalArgumentException("mapperInterface is not interface.");
        }
    }


    /**
     * Invoke.
     *
     * @param proxy  the proxy
     * @param method the method
     * @param args   the args
     * @return the object
     * @throws Throwable the throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class caller = method.getDeclaringClass();


        String methodName = method.getName();
        if(methodName.equals("toString")){
        	return "BeetlSql Mapper "+mapperInterface;
        }
//        SqlResource resource  =  method.getDeclaringClass().getAnnotation(SqlResource.class);
        SqlResource resource = (SqlResource)this.mapperInterface.getAnnotation(SqlResource.class);
        String sqlId = null;
        if(resource!=null){
        		String preffix = resource.value();
        		String name = method.getName();
        		sqlId = preffix+"."+name;
        }else{
        		sqlId = this.builder.getIdGen().getId(method.getDeclaringClass(),entityClass, method);
            
        }
        
       
        
        MapperInvoke invoke = sqlManager.getMapperConfig().getAmi(caller, methodName);
        if (invoke != null) {
            //内置的方法，直接调用Invoke
            return invoke.call(this.sqlManager, this.entityClass, sqlId, method, args);

        } else {
            //解析方法以及注解，找到对应的处理类
            MethodDesc desc = MethodDesc.getMetodDesc(sqlManager, this.entityClass, method, sqlId);
            if (desc.sqlReady.length() == 0) {
                invoke = MapperInvokeDataConfig.getMethodDescProxy(desc.type);
                Object ret = invoke.call(this.sqlManager, this.entityClass, sqlId, method, args);
                return ret;
            } else {
                invoke = MapperInvokeDataConfig.getSQLReadyProxy();;
                Object ret = invoke.call(this.sqlManager, this.entityClass, desc.sqlReady, method, args);
                return ret;
            }

        }


    }
    
    public String toString(){
    	return " Proxy";
    }


}
