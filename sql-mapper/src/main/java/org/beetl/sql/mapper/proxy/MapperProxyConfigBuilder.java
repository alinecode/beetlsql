package org.beetl.sql.mapper.proxy;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.BaseMapperConfigBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 允许在jdk代理基础上，再代理
 */
@Plugin
public class MapperProxyConfigBuilder extends BaseMapperConfigBuilder {
	public MapperProxyConfigBuilder(){
		super();
	}

	/**
	 * 返回一个代理类{@code MapperInvokeWrapper}，有机会在执行mapper方法执行前
	 * @param old
	 * @param method
	 * @return
	 */
	@Override
	protected MapperInvoke wrap(MapperInvoke old, Method method){
		Annotation config = null;
		config = BeanKit.getMethodAnnotation(method, MapperProxy.class);
		if(config==null){
			config = BeanKit.getClassAnnotation(method.getDeclaringClass(), MapperProxy.class);
			if(config==null){
				return old;
			}
		}


		MapperProxy mapperProxy = config.annotationType().getAnnotation(MapperProxy.class);
		MapperProxyExecutor executor = BeanKit.newSingleInstance(mapperProxy.value());
		MapperInvokeWrapper wrapper = new MapperInvokeWrapper(old,config,executor);
		return wrapper;
	}


	static class MapperInvokeWrapper  extends MapperInvoke{
		MapperInvoke old ;
		Annotation config;
		MapperProxyExecutor executor;
        public MapperInvokeWrapper(MapperInvoke old,Annotation config, MapperProxyExecutor executor){
        	this.old = old;
        	this.config = config;
        	this.executor = executor;
		};

		@Override
		public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
			ProxyContext proxyContext = new ProxyContext();
			proxyContext.setSqlManager(sm);
			proxyContext.setMapperInvoke(old);
			proxyContext.setMethod(m);
			proxyContext.setConfig(config);
			proxyContext.setArgs(args);

			executor.before(proxyContext);
			Object ret =  old.call(sm,entityClass,m,args);
			executor.after(proxyContext,ret);
			return ret;
		}
	}
}
