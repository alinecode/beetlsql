package com.beetl.sql.rewrite.mapper;

import com.beetl.sql.rewrite.SqlRewriteInterceptor;
import com.beetl.sql.rewrite.annotation.DisableRewrite;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperJava8Proxy;
import org.beetl.sql.mapper.builder.MapperConfigBuilder;

import java.lang.reflect.Method;

/**
 * 自有从TenantBaseMapper发出的任何查询，才会触发sql解析和重写 SqlRewriteInterceptor
 */
public class RewriteMapperJava8Proxy extends MapperJava8Proxy {

	SqlRewriteInterceptor sqlRewriteInterceptor;


	public RewriteMapperJava8Proxy(MapperConfigBuilder builder, SQLManager sqlManager, Class<?> mapperInterface,SqlRewriteInterceptor sqlRewriteInterceptor){
		super(builder,sqlManager,mapperInterface);
		this.sqlRewriteInterceptor = sqlRewriteInterceptor;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (proxy instanceof  RewriteBaseMapper) {
			DisableRewrite disableRewrite = method.getAnnotation(DisableRewrite.class);
			if(disableRewrite==null){
				//开启重写
				sqlRewriteInterceptor.enable();
			}
		}
		try {
			return super.invoke(proxy, method, args);
		} finally {
			sqlRewriteInterceptor.reset();
		}
	}
}
