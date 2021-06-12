package org.beetl.sql.mapper.proxy;

/**
 * 子类实现
 */
public interface MapperProxyExecutor {
	 default void before(ProxyContext proxyContext){
	 	return ;
	 }
	 default Object after(ProxyContext proxyContext,Object ret){
	 	return  ret;
	 }
}
