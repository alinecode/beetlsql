package org.beetl.sql.ext;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 尝试用一个Map实现简单的缓存.(最终想集成redis)
 * 
 * @author zhoupan
 */
public class SimpleCacheInterceptor implements Interceptor {

	/** The Constant logger. */
	public final Logger logger = LoggerFactory.getLogger(SimpleCacheInterceptor.class);

	/**
	 * The Constructor.
	 */
	public SimpleCacheInterceptor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.sql.core.Interceptor#before(org.beetl.sql.core.
	 * InterceptorContext)
	 */
	public void before(InterceptorContext ctx) {
		String cacheKey = this.getCacheKey(ctx);
		Object cacheObject;
		try {
			cacheObject = this.getCacheObject(cacheKey);
			ctx.setResult(cacheObject);
			// TODO:SQLManager 判断result不为null的时候,直接返回.
		} catch (Throwable e) {
			logger.error("get cache object with cache key:{} exception:{}", cacheKey, e);
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.sql.core.Interceptor#after(org.beetl.sql.core.
	 * InterceptorContext)
	 */
	public void after(InterceptorContext ctx) {
		// 清缓存
		if (ctx.isUpdate()) {
			this.clearCache(ctx);
		} else {
			// 缓存结果.
			this.putCache(ctx);
		}
	}

	/** The cache. */
	Map<String, Object> cache = new java.util.concurrent.ConcurrentHashMap<String, Object>();

	/**
	 * Gets the cache key.
	 *
	 * @param ctx
	 *            the ctx
	 * @return the cache key
	 */
	public String getCacheKey(InterceptorContext ctx) {
		return this.getCacheKey(ctx.getSql(), ctx.getSql(), ctx.getParas());
	}

	/**
	 * Gets the cache key.
	 *
	 * @param sqlId
	 *            the sql id
	 * @param sql
	 *            the sql
	 * @param paras
	 *            the paras
	 * @return the cache key
	 */
	public String getCacheKey(String sqlId, String sql, List<Object> paras) {
		StringBuilder sb = new StringBuilder();
		sb.append("sqlId : " + sqlId).append("\n").append("sql   : " + sql).append("\nparas : " + paras);
		return sb.toString();
	}

	/**
	 * Gets the cache object.
	 *
	 * @param cacheKey
	 *            the cache key
	 * @return the cache object
	 * @throws Exception
	 *             the exception
	 */
	public Object getCacheObject(String cacheKey) throws Exception {
		logger.debug("get cache object by cacheKey:{}", cacheKey);
		return this.cache.get(cacheKey);
	}

	/**
	 * Put cache object.
	 *
	 * @param cacheKey
	 *            the cache key
	 * @param cacheObject
	 *            the cache object
	 * @throws Exception
	 *             the exception
	 */
	public void putCacheObject(String cacheKey, Object cacheObject) throws Exception {
		logger.debug("put cache object cacheKey:{} cacheObject:{}", cacheKey, cacheObject);
		this.cache.put(cacheKey, cacheObject);
	}

	/**
	 * Removes the cache object.
	 *
	 * @param cacheKey
	 *            the cache key
	 * @throws Exception
	 *             the exception
	 */
	public void removeCacheObject(String cacheKey) throws Exception {
		logger.debug("remove cache object cacheKey:{}", cacheKey);
		this.cache.remove(cacheKey);
	}

	/**
	 * Clear cache.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void clearCache(InterceptorContext ctx) {
		logger.debug("clear cache for sqlId:{}", ctx.getSqlId());
		this.cache.clear();
	}

	/**
	 * Put cache.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void putCache(InterceptorContext ctx) {
		// 缓存内容.
		String cacheKey = this.getCacheKey(ctx);
		try {
			this.putCacheObject(cacheKey, ctx.getResult());
		} catch (Exception e) {
			logger.error("put cache object with cache key:{} exception:{}", cacheKey, e);
		}
	}

}
