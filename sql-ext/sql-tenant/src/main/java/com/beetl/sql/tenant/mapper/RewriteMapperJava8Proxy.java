package com.beetl.sql.tenant.mapper;

import com.beetl.sql.tenant.SqlRewriteInterceptor;
import com.beetl.sql.tenant.annotation.DisableRewrite;
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
		Class caller = method.getDeclaringClass();
		if (RewriteBaseMapper.class.isAssignableFrom(caller)) {
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
