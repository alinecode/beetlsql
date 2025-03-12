package com.beetl.sql.rewrite;

import com.beetl.sql.rewrite.mapper.RewriteMapperBuilder;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.PluginExtConfig;

import java.util.*;

/**
 * 配置sqlmanager 支持多租户，自动在sql语句增加租户
 */
public class RewriteConfig implements PluginExtConfig {
	SqlRewriteInterceptor sqlRewriteInterceptor;
	@Override
	public void config(SQLManager sqlManager) {
		sqlRewriteInterceptor = new SqlRewriteInterceptor(sqlManager);
		Interceptor[] olds = sqlManager.getInters() ;
		if(olds==null||olds.length==0){
			sqlManager.setInters(new Interceptor[]{sqlRewriteInterceptor});
			return ;
		}

		List list = new ArrayList<>(Arrays.asList(olds));
		list.add(0,sqlRewriteInterceptor);
		sqlManager.setInters((Interceptor[]) list.toArray(new Interceptor[0]));

		//所有 从 TenantBaseMapper的调用，才触发sqlParser
		RewriteMapperBuilder tenantMapperBuilder = new RewriteMapperBuilder(sqlRewriteInterceptor);
		tenantMapperBuilder.setSqlManager(sqlManager);
		sqlManager.setMapperBuilder(tenantMapperBuilder);
	}

	/**
	 * 开启缓存，缓存用WeakHashMap
	 */
	public void enableCache(){
		WeakHashMap<String, String> tempMap =new WeakHashMap();
		Map cache = Collections.synchronizedMap(tempMap);
		sqlRewriteInterceptor.setSqlCache(cache);

	}

	/**
	 * 开启缓存，自定义一个cache类
	 * @param cache
	 */
	public void enableCache(Map cache){
		sqlRewriteInterceptor.setSqlCache(cache);

	}

	public void addColRewriteConfig(ColRewriteParam colRewriteParam){
		sqlRewriteInterceptor.getRewriteConfigs().add(colRewriteParam);
	}
}
