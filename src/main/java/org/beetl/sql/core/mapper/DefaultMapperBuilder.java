package org.beetl.sql.core.mapper;

import org.beetl.sql.core.SQLIdNameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 默认Java代理实现.
 * 
 * @author zhoupan
 */
public class DefaultMapperBuilder implements MapperBuilder {

	/** The cache. */
	protected Map<Class<?>, Object> cache = new java.util.concurrent.ConcurrentHashMap<Class<?>, Object>();

	/** The sql manager. */
	protected SQLManager sqlManager;

	protected ClassLoader entityClassLoader;
	

	/**
	 * The Constructor.
	 *
	 * @param sqlManager
	 *            the sql manager
	 */
	public DefaultMapperBuilder(SQLManager sqlManager) {
		super();
		this.sqlManager = sqlManager;
		this.entityClassLoader = sqlManager.getEntityLoader();
	}

	/**
	 * The Constructor.,不推荐使用，使用SQLManager.setEntityLoader()
	 *
	 * @param sqlManager
	 *            the sql manager
	 * @param classLoader
	 * 			  specified class loader for loading mapped entities
	 */
	@Deprecated
	public DefaultMapperBuilder(SQLManager sqlManager, ClassLoader classLoader) {
		this(sqlManager);
		this.entityClassLoader = classLoader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.sql.ext.dao2.MapperBuilder#getMapper(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> mapperInterface) {
		if (cache.containsKey(mapperInterface)) {
			return (T) cache.get(mapperInterface);
		} else {
			T instance = this.buildInstance(mapperInterface);
			cache.put(mapperInterface, instance);
			return instance;
		}
	}

	/**
	 * Builds the instance.
	 *
	 * @param <T>
	 *            the generic type
	 * @param mapperInterface
	 *            the dao2 interface
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T buildInstance(Class<T> mapperInterface) {
		ClassLoader loader =  sqlManager.getEntityLoader();
		//当没有指定ClassLoader的情况下使用ContextLoader，适合大多数框架
		if (BeanKit.queryLambdasSupport) {
		    return (T) Proxy.newProxyInstance(loader==null?this.getClass().getClassLoader():loader, new Class<?>[] { mapperInterface },
	                new MapperJava8Proxy(this,sqlManager, mapperInterface));
		}else {
		    return (T) Proxy.newProxyInstance(loader==null?this.getClass().getClassLoader():loader, new Class<?>[] { mapperInterface },
	                new MapperJavaProxy(this,sqlManager, mapperInterface));
		}
	
	}

	public SQLIdNameConversion getIdGen() {
		return  sqlManager.getSQLIdNameConversion();
	}

	
	
	
}
