package org.beetl.sql.mapper.wrapper;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.BaseMapperConfigBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
@Plugin
public class WrapperConfigBuilder extends BaseMapperConfigBuilder {
	public WrapperConfigBuilder(){
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
		Annotation config = BeanKit.getAnnotation(method, MapperWrapper.class);
		if(config==null){
			return old;
		}

		MapperWrapper mapperWrapper = config.annotationType().getAnnotation(MapperWrapper.class);
		MapperWrapperExecutor executor = BeanKit.newSingleInstance(mapperWrapper.value());
		MapperInvokeWrapper wrapper = new MapperInvokeWrapper(old,config,executor);
		return wrapper;
	}


	static class MapperInvokeWrapper  extends MapperInvoke{
		MapperInvoke old ;
		Annotation config;
		MapperWrapperExecutor executor;
        public MapperInvokeWrapper(MapperInvoke old,Annotation config,MapperWrapperExecutor executor){
        	this.old = old;
        	this.config = config;
        	this.executor = executor;
		};

		@Override
		public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
			WrapperContext wrapperContext = new WrapperContext();
			wrapperContext.setSqlManager(sm);
			wrapperContext.setMapperInvoke(old);
			wrapperContext.setMethod(m);
			wrapperContext.setConfig(config);
			wrapperContext.setArgs(args);

			executor.before(wrapperContext);
			Object ret =  old.call(sm,entityClass,m,args);
			executor.after(wrapperContext,ret);
			return ret;
		}
	}
}
