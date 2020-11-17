package org.beetl.sql.ext;

import lombok.Data;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.SQLParameter;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 只能用于单体应用的dao缓存。 尝试用一个Map实现简单的缓存.如果想使用其他实现，可以实现CacheManager方法
 * 如果跟缓存相关的实体被修改，则缓存全部清空，如果只想清空跟实体相关的缓存，需要重载
 * clearCache(String ns)
 * <p></p>
 * 注意，对于直接调用模板sql或者jdbc sql(execute系列方法）此缓存不起作用，因为没有sqlId，还不能判断作用于哪些实体
 *
 * @author zhoupan, xiandafu
 */
public class SimpleCacheInterceptor implements Interceptor {


	/** The cache. */
	Set<String> nsSet = null;
	CacheManager cm = null;

	/** 用MapCacheManager来实现缓存
	 * @param namespaces 需要考虑缓存的实体
	 */
	public SimpleCacheInterceptor(List<String> namespaces) {
		this(namespaces, new MapCacheManager());
	}

	/**
	 *
	 * @param namespaces
	 * @param cm  指定的缓存管理
	 */
	public SimpleCacheInterceptor(List<String> namespaces, CacheManager cm) {
		this.cm = cm;
		nsSet = new HashSet<String>();
		for (String ns : namespaces) {

			nsSet.add(ns);
			this.cm.initCache(ns);
		}
	}


	@Override
	public void before(InterceptorContext ctx) {
		ExecuteContext executeContext = ctx.getExecuteContext();
		String ns = executeContext.sqlId.getNamespace();
		if (!this.cacheRequire(ns)) {
			return;
		}
		ctx.put("cache.required", Boolean.TRUE);
		ctx.put("cache.ns", ns);

		if (executeContext.isUpdate) {
			return;
		}

		Object cacheKey = this.getCacheKey(ctx);
		ctx.put("cache.key", cacheKey);
		Object cacheObject;

		cacheObject = this.getCacheObject(ns, cacheKey);
		if (cacheObject == null) {
			//未找到，有sqlscript触发一次真正的查询
			ctx.put("cache.hit", Boolean.FALSE);
		} else {
			ctx.put("cache.hit", Boolean.TRUE);
			ctx.getExecuteContext().executeResult = cacheObject;
		}

		return;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.beetl.sql.core.Interceptor#after(org.beetl.sql.core.
	 * InterceptorContext)
	 */
	@Override
	public void after(InterceptorContext ctx) {
		if (ctx.get("cache.required") == null) {
			return;
		}
		String ns = (String) ctx.get("cache.ns");
		// 清缓存
		if (ctx.getExecuteContext().isUpdate) {
			this.clearCache(ns);
		} else {
			// 缓存结果.
			Boolean hit = (Boolean) ctx.get("cache.hit");
			if (!hit) {
				//如果没有命中缓存，则需要将结果放入到缓存里
				Object key = ctx.get("cache.key");
				this.putCache(ns, key, ctx);
			}

		}

	}

	/**
	 * Gets the cache key.
	 *
	 * @param ctx
	 *            the ctx
	 * @return the cache key
	 */
	public Object getCacheKey(InterceptorContext ctx) {
		ExecuteContext executeContext = ctx.getExecuteContext();
		return this.getCacheKey(executeContext.sqlId, executeContext.sqlResult.jdbcPara);
	}


	protected Object getCacheKey(SqlId sqlId, List<SQLParameter> paras) {
		return new CacheKey(sqlId, paras);
	}

	/**
	 *  获取缓存对象
	 * @param ns
	 * @param cacheKey
	 * @return
	 */
	public Object getCacheObject(String ns, Object cacheKey) {
		return this.cm.getCache(ns, cacheKey);

	}


	/**
	 * 清除缓存对象
	 * @param ns
	 */
	public void clearCache(String ns) {
		this.cm.clearCache(ns);

	}

	/**
	 * 设置缓存对象
	 * @param ns
	 * @param key
	 * @param ctx
	 */
	public void putCache(String ns, Object key, InterceptorContext ctx) {
		//		 缓存内容.
		this.cm.putCache(ns, key, ctx.getExecuteContext().executeResult);
	}


	protected boolean cacheRequire(String ns) {
		return this.nsSet.contains(ns);
	}

	public CacheManager getCacheManger() {
		return cm;
	}

	public Set<String> getNsSet() {
		return nsSet;
	}

	public boolean containCache(String ns, Object key) {
		return this.cm.containCache(ns, key);
	}

	@Override
	public void exception(InterceptorContext ctx, Exception ex) {

	}

	public class CacheKey {
		SqlId sqlId;
		List paras;

		public CacheKey(SqlId sqlId, List paras) {
			this.sqlId = sqlId;
			this.paras = paras;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			CacheKey cacheKey = (CacheKey) o;
			return sqlId.equals(cacheKey.sqlId) && paras.equals(cacheKey.paras);
		}

		@Override
		public int hashCode() {
			return Objects.hash(sqlId, paras);
		}
	}

	public interface CacheManager {
		void initCache(String ns);

		void putCache(String ns, Object key, Object value);

		Object getCache(String ns, Object key);

		void clearCache(String ns);

		boolean containCache(String ns, Object key);
	}

	public static class MapCacheManager implements CacheManager {
		Map<String, Map<Object, Object>> cache = new ConcurrentHashMap<String, Map<Object, Object>>();

		public void initCache(String ns) {
			cache.put(ns, new ConcurrentHashMap<Object, Object>());
		}

		@Override
		public void putCache(String ns, Object key, Object value) {
			this.cache.get(ns).put(key, value);

		}

		@Override
		public Object getCache(String ns, Object key) {
			return this.cache.get(ns).get(key);
		}

		@Override
		public void clearCache(String ns) {
			//清除所有缓存，避免关联带来数据不一致
			for (Entry<String, Map<Object, Object>> entry : this.cache.entrySet()) {
				entry.getValue().clear();
			}
		}

		@Override
		public boolean containCache(String ns, Object key) {
			return this.cache.get(ns).containsKey(key);
		}

	}


}
