package com.beetl.sql.tenant.mapper;

import com.beetl.sql.tenant.SqlRewriteInterceptor;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.mapper.DefaultMapperBuilder;

import java.lang.reflect.Proxy;

public class RewriteMapperBuilder extends DefaultMapperBuilder {

	SqlRewriteInterceptor sqlRewriteInterceptor;
	public RewriteMapperBuilder(SqlRewriteInterceptor sqlRewriteInterceptor){
		this.sqlRewriteInterceptor = sqlRewriteInterceptor;
	}

	@Override
	public <T> T buildInstance(Class<T> mapperInterface) {
		ClassLoaderKit loader = sqlManager.getClassLoaderKit();
		if (BeanKit.queryLambdasSupport) {
			return (T) Proxy.newProxyInstance(loader.getPreferredLoader(), new Class<?>[]{mapperInterface},
				new RewriteMapperJava8Proxy(mapperConfig, sqlManager, mapperInterface,sqlRewriteInterceptor));
		} else {
			throw new UnsupportedOperationException("不支持Jdk8一下版本");
		}

	}
}
