package org.beetl.sql.ext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 尝试用一个Map实现简单的缓存.(最终想集成redis)
 * 
 * 
 * 
 * @author zhoupan,xiandafu
 */
public class SimpleCacheInterceptor implements Interceptor {

	/** The Constant logger. */
	public final Logger logger = LoggerFactory.getLogger(SimpleCacheInterceptor.class);

	/** The cache. */
	Map<String, Map<String,Object>> cache = new ConcurrentHashMap<String,  Map<String,Object>>();
	Set<String> nsSet = null;
	/**
	 * The Constructor.
	 */
	public SimpleCacheInterceptor() {
		nsSet = Collections.emptySet();
	}
	/**
	 * 哪些实体类会被考虑缓存
	 * @param clsz
	 */
	public SimpleCacheInterceptor(List<Class> entitys) {
		nsSet = new HashSet<String>();
		for(Class c:entitys){
			String name = c.getSimpleName();
			//TODO dbstyle 里做这个转化
			nsSet.add(StringKit.toLowerCaseFirstOne(name));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.sql.core.Interceptor#before(org.beetl.sql.core.
	 * InterceptorContext)
	 */
	public void before(InterceptorContext ctx) {
		String ns = this.getSqlIdNameSpace(ctx.getSqlId());
		if(this.cacheRequire(ns)){
			return ;
		}
		ctx.put("cache.required", Boolean.TRUE);
		ctx.put("cache.ns", ns);
		
		if(!ctx.isUpdate()){
			return;
		}
		
		String cacheKey = this.getCacheKey(ctx);
		ctx.put("cache.key", cacheKey);
		Object cacheObject;
		try {
			
			cacheObject = this.getCacheObject(ns,cacheKey);
			ctx.setResult(cacheObject);
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
		if(ctx.get("cache.required")==null){
			return ;
		}
		String ns = (String) ctx.get("cache.ns");
		// 清缓存
		if (ctx.isUpdate()) {
			this.clearCache(ns);
		} else {
			// 缓存结果.
			String key = (String)ctx.get("cache.cacheKey");
			this.putCache(ns,key,ctx);
		}
		
	}

	/**
	 * Gets the cache key.
	 *
	 * @param ctx
	 *            the ctx
	 * @return the cache key
	 */
	public String getCacheKey(InterceptorContext ctx) {
		return this.getCacheKey(ctx.getSqlId(), ctx.getParas());
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
	private  String getCacheKey(String sqlId, List<Object> paras) {
		StringBuilder sb = new StringBuilder();
		sb.append("sqlId : " + sqlId).append("\nparas : " + paras);
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
	public Object getCacheObject(String ns,String cacheKey) throws Exception {
		logger.debug("get cache object by cacheKey:{}", cacheKey);
		Map<String,Object>  map = this.cache.get(ns);
		if(map!=null){
			map = new ConcurrentHashMap<String,Object>();
			this.cache.put(ns,map);
		}
		if(!map.containsKey(cacheKey)){
			return null;
		}
		
		return map.get(cacheKey);
		
		
	}
	

	/**
	 * Clear cache.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void clearCache(String ns) {
		this.cache.get(ns).clear();
	}

	/**
	 * Put cache.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void putCache(String ns,String key,InterceptorContext ctx) {
		// 缓存内容.
		this.cache.get(ns).put(key, ctx.getResult());
		
	}
	
	protected String getSqlIdNameSpace(String sqlId){
		int index =sqlId.indexOf('.');
		return sqlId.substring(0, index);
	}
	
	protected boolean cacheRequire(String ns){
		return this.nsSet.contains(ns);
	}
	
	

}
