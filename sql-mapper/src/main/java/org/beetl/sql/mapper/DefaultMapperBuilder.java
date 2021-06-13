package org.beetl.sql.mapper;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.MapperBuilder;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.builder.BaseMapperConfigBuilder;
import org.beetl.sql.mapper.builder.MapperConfigBuilder;
import org.beetl.sql.mapper.proxy.MapperProxyConfigBuilder;

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

	protected MapperConfigBuilder mapperConfig =  new BaseMapperConfigBuilder();
//	protected MapperConfigBuilder mapperConfig =  new MapperProxyConfigBuilder();


	public DefaultMapperBuilder() {
		super();
	}

	/**
	 * The Constructor.
	 *
	 * @param sqlManager
	 *            the sql manager
	 */
	public DefaultMapperBuilder(SQLManager sqlManager) {
		super();
		this.sqlManager = sqlManager;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.sql.ext.dao2.MapperBuilder#getMapper(java.lang.Class)
	 */
	//Override
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> mapperInterface) {
		if(sqlManager==null){
			throw new IllegalStateException("SQLManager is null");
		}
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
		ClassLoaderKit loader = sqlManager.getClassLoaderKit();
		if (BeanKit.queryLambdasSupport) {
		    return (T) Proxy.newProxyInstance(loader.getPreferredLoader(), new Class<?>[] { mapperInterface },
	                new MapperJava8Proxy(mapperConfig,sqlManager, mapperInterface));
		}else {
		    return (T) Proxy.newProxyInstance(loader.getPreferredLoader(), new Class<?>[] { mapperInterface },
	                new MapperJavaProxy(mapperConfig,sqlManager, mapperInterface));
		}
	
	}



	@Override
	public SQLManager getSqlManager() {
		return sqlManager;
	}

	@Override
	public void setSqlManager(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public MapperConfigBuilder getMapperConfig() {
		return mapperConfig;
	}

	public void setMapperConfig(MapperConfigBuilder mapperConfig) {
		this.mapperConfig = mapperConfig;
	}
}
