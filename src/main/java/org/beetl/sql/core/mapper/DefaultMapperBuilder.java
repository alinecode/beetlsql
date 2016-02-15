package org.beetl.sql.core.mapper;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.beetl.sql.core.SQLManager;

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
	
	protected SqlIdGenerator  idGen  = new DefaultSqlIdGenerator();

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
	 * @see org.beetl.sql.ext.mapper.MapperBuilder#getMapper(java.lang.Class)
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
	 *            the mapper interface
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T buildInstance(Class<T> mapperInterface) {
		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { mapperInterface },
				new MapperJavaProxy(this,sqlManager, mapperInterface));
	}

	public SqlIdGenerator getIdGen() {
		return idGen;
	}

	public void setIdGen(SqlIdGenerator idGen) {
		this.idGen = idGen;
	}
	
	
}
