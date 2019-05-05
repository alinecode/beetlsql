package org.beetl.sql.core.mapper;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.annotatoin.*;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.kit.StringKit;
import org.beetl.sql.core.mapper.builder.MapperConfig;
import org.beetl.sql.core.mapper.builder.MapperInvokeDataConfig;
import org.beetl.sql.core.mapper.para.PageQueryParamter;


import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
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


    protected DefaultMapperBuilder builder;


    protected MapperConfig mapperConfig;

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
        if (methodName.equals("toString")) {
            return "BeetlSql Mapper " + mapperInterface;
        }

        SqlResource resource = getSqlResourece(method);
        String sqlId = null;
        if (resource != null) {
            String preffix = resource.value();
            String name = method.getName();
            sqlId = preffix + "." + name;
        } else {
            //内置的
            sqlId = this.builder.getIdGen().getId(method.getDeclaringClass(), entityClass, method);

        }


        MapperInvoke invoke = sqlManager.getMapperConfig().getAmi(caller, methodName);
        if (invoke != null) {
            //内置的方法，直接调用Invoke
            return invoke.call(this.sqlManager, this.entityClass, sqlId, method, args);

        }

        //解析方法以及注解，找到对应的处理类,先解析jdbc sql

        Sql sqlAnnotation = method.getAnnotation(Sql.class);
        if (sqlAnnotation != null) {

            MethodDesc desc = null;
            String jdbcSql = sqlAnnotation.value();
            Object ret = null;
            if (jdbcSql != null&&jdbcSql.trim().length()!=0) {

                desc = MethodDesc.getMetodDescBySqlReadyProviderWithCache(sqlManager, this.entityClass, method, jdbcSql);
                invoke = MapperInvokeDataConfig.getSQLReadyProxy();
                ret = invoke.call(this.sqlManager, this.entityClass, desc.sqlReady, method, args);


            } else {
                SqlProvider sqlProvider = method.getAnnotation(SqlProvider.class);
                SQLReady sqlReady = getSQLReadyByProvider(sqlProvider, method, args);
                desc = MethodDesc.getMetodDescBySqlReadyProvider(sqlManager, this.entityClass, method, sqlReady.getSql());
                ret = executeSqlReady(desc, sqlReady);
            }

            return ret;

        }


        SqlProvider statementProvider = method.getAnnotation(SqlProvider.class);
        if (statementProvider != null) {
            String sqlTemplate = this.getSQLTemplateProvider(statementProvider, method, args);
            MethodDesc desc = MethodDesc.getMetodDescByTemplateProvider(sqlManager, this.entityClass, method, sqlTemplate);
            return executeSqlTemplate(desc, sqlTemplate, args);

        }

        //最常用情况，sql模板通过md文件提供
        MethodDesc desc = MethodDesc.getMetodDescBySqlId(sqlManager, this.entityClass, method, sqlId);
        invoke = MapperInvokeDataConfig.getMethodDescProxy(desc.type);
        Object ret = invoke.call(this.sqlManager, this.entityClass, sqlId, method, args);
        return ret;


    }


    /**
     * 先从方法上找SqlResource，如果没有，找方法所属类（比如，可能是父类），如果没有，找basemapper定义的
     *
     * @param method
     * @return
     */
    protected SqlResource getSqlResourece(Method method) {
        SqlResource sqlResource = method.getAnnotation(SqlResource.class);
        if (sqlResource != null) {
            return sqlResource;
        }

        sqlResource = method.getDeclaringClass().getAnnotation(SqlResource.class);
        if (sqlResource != null) {
            return sqlResource;
        }

        sqlResource = (SqlResource) this.mapperInterface.getAnnotation(SqlResource.class);
        if (sqlResource != null) {
            return sqlResource;
        }
        return null;
    }

    public String toString() {
        return " Proxy";
    }


    protected Object executeSqlTemplate(MethodDesc desc, String sqlTemplate, Object[] args) {

        int type = desc.type;
        Object para = desc.parameter.get(args);
        if (type == MethodDesc.SM_SELECT_SINGLE || type == MethodDesc.SM_SELECT_LIST) {
            Class returnType = desc.resultType;

            List list = sqlManager.execute(sqlTemplate, desc.resultType, para);
            if (type == MethodDesc.SM_SELECT_SINGLE) {
                return list.size() == 0 ? null : list.get(0);
            } else {
                return list;
            }
        } else if (type == MethodDesc.SM_PAGE_QUERY) {
            PageQuery query = (PageQuery) para;
            query = sqlManager.executePageQuery(sqlTemplate, desc.resultType, query);
            return query;
        } else {
            return sqlManager.executeUpdate(sqlTemplate, para);
        }
    }

    protected Object executeSqlReady(MethodDesc desc, SQLReady sqlReady) {

        int type = desc.type;
        if (type == MethodDesc.SM_SELECT_SINGLE || type == MethodDesc.SM_SELECT_LIST) {
            Class returnType = desc.resultType;
            List list = sqlManager.execute(sqlReady, returnType);
            if (type == MethodDesc.SM_SELECT_SINGLE) {
                return list.size() == 0 ? null : list.get(0);
            } else {
                return list;
            }
        } else if (type == MethodDesc.SM_SQL_READY_PAGE_QUERY) {
            // 分页对象
            PageQueryParamter parameter = (PageQueryParamter) desc.parameter;
            PageQuery pageQuery = (PageQuery) parameter.get(sqlReady.getArgs());
            Object[] jdcbArgs = parameter.getJdbcArgs(sqlReady.getArgs());
            sqlManager.execute(new SQLReady(sqlReady.getSql(), jdcbArgs), entityClass, pageQuery);
            return pageQuery;
        } else {
            return sqlManager.executeUpdate(sqlReady);
        }
    }

    protected SQLReady getSQLReadyByProvider(SqlProvider sqlProvider, Method owner, Object[] args) {
        Class<?> providerCls = null;
        String providerMethodName = null;
        try {
            providerCls = sqlProvider.provider();
            Object provider = PROVIDERS_CACHE.get(providerCls);
            providerMethodName = sqlProvider.method();
            if (StringKit.isBlank(providerMethodName)) {
                providerMethodName = owner.getName();
            }
            if (provider == null) {
                provider = providerCls.newInstance();
                PROVIDERS_CACHE.put(providerCls, provider);
            }
            Method providerMethod = providerCls.getMethod(providerMethodName, owner.getParameterTypes());
            if (providerMethod.getReturnType() != SQLReady.class) {
                throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "SqlProvider类[" + providerCls.getName() + "]的方法[" + providerMethodName + "]返回值必须为SqlReady类型");
            }
            providerMethod.setAccessible(Boolean.TRUE);
            SQLReady sqlReady = (SQLReady) providerMethod.invoke(provider, args);
            return sqlReady;

        } catch (IllegalAccessException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "实例化" + providerCls.getName() + "失败，请检查是否有公有的无参构造");
        } catch (InstantiationException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "实例化" + providerCls.getName() + "失败，请检查是否有公有的无参构造");
        } catch (InvocationTargetException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "调用Provder方法出错" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "未能从" + providerCls.getName() + "获取到" + providerMethodName + " 方法");
        }
    }

    protected String getSQLTemplateProvider(SqlProvider sqlProvider, Method owner, Object[] args) {
        Class<?> providerCls = null;
        String providerMethodName = null;
        try {
            providerCls = sqlProvider.provider();
            Object provider = PROVIDERS_CACHE.get(providerCls);
            providerMethodName = sqlProvider.method();
            if (StringKit.isBlank(providerMethodName)) {
                providerMethodName = owner.getName();
            }
            if (provider == null) {
                provider = providerCls.newInstance();
                PROVIDERS_CACHE.put(providerCls, provider);
            }
            Method providerMethod = providerCls.getMethod(providerMethodName, owner.getParameterTypes());
            if (providerMethod.getReturnType() != String.class) {
                throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "SqlProvider类[" + providerCls.getName() + "]的方法[" + providerMethodName + "]返回值必须为SqlReady类型");
            }
            providerMethod.setAccessible(Boolean.TRUE);
            String sqlTemplate = (String) providerMethod.invoke(provider, args);
            return sqlTemplate;

        } catch (IllegalAccessException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "实例化" + providerCls.getName() + "失败，请检查是否有公有的无参构造");
        } catch (InstantiationException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "实例化" + providerCls.getName() + "失败，请检查是否有公有的无参构造");
        } catch (InvocationTargetException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "调用Provder方法出错" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "未能从" + providerCls.getName() + "获取到" + providerMethodName + " 方法");
        }
    }


}
