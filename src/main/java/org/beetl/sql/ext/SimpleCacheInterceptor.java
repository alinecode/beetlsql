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
 * 如果跟缓存相关的实体被修改，则缓存全部清空，如果只想清空跟实体相关的缓存，需要重载
 * clearCache(String ns)
 * <p></p>
 * 注意，对于直接调用模板sql或者jdbc sql，此缓存不起作用
 * 
 * @author zhoupan,xiandafu
 */
public class SimpleCacheInterceptor implements Interceptor {

	
	/** The cache. */
	Map<String, Map<Object,Object>> cache = new ConcurrentHashMap<String,  Map<Object,Object>>();
	Set<String> nsSet = null;
	
	/**
	 * 哪些实体类会被考虑缓存
	 * @param clsz
	 */
	public SimpleCacheInterceptor(List<Class> entitys) {
		nsSet = new HashSet<String>();
		for(Class c:entitys){
			String name = c.getSimpleName();
			//TODO dbstyle 里做这个转化
			String ns = StringKit.toLowerCaseFirstOne(name);
			nsSet.add(ns);
			cache.put(ns, new ConcurrentHashMap<Object,Object>());
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
		if(!this.cacheRequire(ns)){
			return ;
		}
		ctx.put("cache.required", Boolean.TRUE);
		ctx.put("cache.ns", ns);
		
		if(ctx.isUpdate()){
			return;
		}
		
		Object cacheKey = this.getCacheKey(ctx);
		ctx.put("cache.key", cacheKey);
		Object cacheObject;
		
		cacheObject = this.getCacheObject(ns,cacheKey);
		ctx.setResult(cacheObject);
		
		
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
			Object key = (Object)ctx.get("cache.key");
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
	public Object getCacheKey(InterceptorContext ctx) {
		return this.getCacheKey(ctx.getSqlId(), ctx.getSql(),ctx.getParas());
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
	private  Object getCacheKey(String sqlId, String sql,List<Object> paras) {
		StringBuilder sb = new StringBuilder();
		sb.append("sqlId : " + sqlId).append("\nsql:").append(sql).append("\nparas : " + paras);
		//TODO:性能有点慢
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
	public Object getCacheObject(String ns,Object cacheKey)  {
		Map<Object,Object>  map = this.cache.get(ns);
		return map.get(cacheKey);
		
	}
	

	/**
	 * 清楚所有缓存
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void clearCache(String ns) {
		this.cache.clear();
		
	}

	/**
	 * Put cache.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void putCache(String ns,Object key,InterceptorContext ctx) {
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
